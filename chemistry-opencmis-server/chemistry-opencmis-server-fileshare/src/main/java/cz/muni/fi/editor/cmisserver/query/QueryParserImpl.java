package cz.muni.fi.editor.cmisserver.query;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.query.QueryUtilStrict;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public class QueryParserImpl implements QueryParser
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryParserImpl.class);
    private QueryWalkerFactory walkerFactory = new QueryWalkerFactory();
    private Query result;

    @Override
    public Query parseQuery(String statement, TypeManager typeManager) throws RecognitionException
    {
        QueryUtilStrict queryUtilStrict = new QueryUtilStrict(statement, typeManager, null);
        CommonTree ct = queryUtilStrict.parseStatement();
        queryUtilStrict.walkStatement();
        // we can obtain columns, order, table etc. from query object
        // todo fetch 'table' as type
        //QueryObject queryObject = queryUtilStrict.getQueryObject();

        /*
        FIXME: for some yet unknown reason queryObject does not contain the comparison operation
        from where branch as it should. therefore we need to parse the tree manually. if this get fixed
        or resolved why it does not work (or i think it should work) custom tree walker can be removed.
         */

        walkTheTree(ct, 0);

        return result;
    }

    private void walkTheTree(Tree tree, int level)
    {
        if (LOG.isTraceEnabled())
        {
            LOG.trace("L:- {}, T:- {}, V:- {}", level, tree.getType(), tree.toStringTree());
        }
        for (int i = 0; i < tree.getChildCount(); i++)
        { //where should be at level 3 but nested queries can mess things up
            if (tree.getType() == 69)
            {
                QueryWalker walker = walkerFactory.getWalker();
                walker.handleWhereBranch(tree);
                result = walker.obtainQuery();

                break;
            }
            else if (result == null)
            {
                walkTheTree(tree.getChild(i), level + 1);
            }
        }
    }
}

