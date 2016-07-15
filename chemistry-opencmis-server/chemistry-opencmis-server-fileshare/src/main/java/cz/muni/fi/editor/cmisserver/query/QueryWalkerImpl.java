package cz.muni.fi.editor.cmisserver.query;

import cz.muni.fi.editor.cmisserver.query.dsl.QueryBuilderFactory;
import org.antlr.runtime.tree.Tree;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class QueryWalkerImpl implements QueryWalker
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryWalkerImpl.class);
    private static final Set<Integer> LITERALS = new HashSet<>(4);
    private static final QueryBuilderFactory qbf = new QueryBuilderFactory();

    private Query finalLuceneQuery;

    public QueryWalkerImpl()
    {
        LITERALS.add(SQLConstants.LITERALS.BOOL_LIT);
        LITERALS.add(SQLConstants.LITERALS.NUMBER_LIT);
        LITERALS.add(SQLConstants.LITERALS.STRING_LIT);
        LITERALS.add(SQLConstants.LITERALS.TIME_LIT);
    }

    @Override
    public void handleWhereBranch(Tree tree) throws IllegalArgumentException
    {
        if (tree.getType() != 69)
        {
            throw new IllegalArgumentException("This not WHERE branch. Where branch has typeID=69 but was " + tree.getType());
        }
        else
        {
            finalLuceneQuery = walkPredicate(tree.getChild(0));
        }
    }

    @Override
    public Query obtainQuery() throws IllegalStateException
    {
        if(finalLuceneQuery == null){
            throw new IllegalStateException("Query is null, did tou call handleWhereBranch() first ?");
        }
        return finalLuceneQuery;
    }

    private Query walkPredicate(Tree tree)
    {
        LOG.debug("SUBTREE TYPE " + tree.getType());
        switch (tree.getType())
        {
            case SQLConstants.LOGIC.AND:
                return walkAnd(tree, tree.getChild(0), tree.getChild(1));
            case SQLConstants.LOGIC.OR:
                walkOr(tree, tree.getChild(0), tree.getChild(1));
                break;
            case SQLConstants.COMPARATORS.EQ:
            case SQLConstants.COMPARATORS.NEQ:
                return walkQuals(tree, tree.getChild(0), tree.getChild(1));
        }

        return null;
    }

    private Query walkAnd(Tree operator, Tree left, Tree right)
    {
        Query q = qbf.qb().bool().must(walkPredicate(left)).must(walkPredicate(right)).createQuery();
        if (LOG.isDebugEnabled())
        {
            LOG.debug("AND brannch for L:-[{},{}] and R:-[{},{}]", left.getType(), left.getText(), right.getType(), right.getText());
            LOG.debug("LuceneQ:-    {}", q);
        }

        return q;
    }

    private void walkOr(Tree operator, Tree left, Tree right)
    {
        LOG.debug("OR");
        LOG.debug("left: {}", left);
        LOG.debug("right: {}", right);
    }


    private Query walkQuals(Tree operator, Tree left, Tree right)
    {
        if (operator.getType() == SQLConstants.COMPARATORS.EQ)
        {
            String[] data = readDescendants(left, right);
            Query q = qbf.qb().keyword().on(data[0]).matching(data[1]).createQuery();
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Q(type:{}) :-  {} equals {}", operator.getType(), data[0], data[1]);
                LOG.debug("LuceneQ:- {}", q);
            }
            return q;
        }
        else if (operator.getType() == SQLConstants.COMPARATORS.NEQ)
        {
            String[] data = readDescendants(left, right);
            Query q = qbf.qb().bool().must(qbf.qb().keyword().on(data[0]).matching(data[1]).createQuery()).not().createQuery();
            ;
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Q(type:{}) :-  {} notequals {}", operator.getType(), data[0], data[1]);
                LOG.debug("LuceneQ:- {}", q);
            }

            return q;
        }
        else
        {
            throw new IllegalArgumentException("not a 'equals' or 'notequals'");
        }
    }

    /**
     * returns arrays of values where on first position is column name and on the second one is the compared value
     *
     * @param left  node of parent
     * @param right right node of parent
     * @return array of 2 elements containing column name and compared value
     */
    private String[] readDescendants(Tree left, Tree right)
    {
        String[] result = new String[2];
        if (isColumn(left))
        {
            result[0] = getColumn(left);
            result[1] = readConditionValue(right);
        }
        else
        {
            result[0] = getColumn(right);
            result[1] = readConditionValue(left);
        }

        return result;
    }

    private String getColumn(Tree node)
    {
        if (node.getType() != SQLConstants.COL)
        {
            throw new IllegalArgumentException("Given node is not a column. Expected typeID=" + SQLConstants.COL + " but was " + node.getType());
        }

        return node.getChild(0).getText();
    }


    private boolean isColumn(Tree tree)
    {
        return tree.getType() == SQLConstants.COL;
    }


    private String readConditionValue(Tree node)
    {
        if (!LITERALS.contains(node.getType()))
        {
            throw new IllegalArgumentException("Given node is not a literal type. Expected typeID=" + LITERALS + " but was " + node.getType());
        }

        return node.getText();

        // is this needed at all ?
//        switch (node.getType()){
//            case SQLConstants.LITERALS.BOOL_LIT:
//                return Boolean.valueOf(node.getText()).toString();
//            case SQLConstants.LITERALS.NUMBER_LIT:
//                //todo
//                if(node.getText().contains(".")){
//                    return
//                }
//        }
    }
}
