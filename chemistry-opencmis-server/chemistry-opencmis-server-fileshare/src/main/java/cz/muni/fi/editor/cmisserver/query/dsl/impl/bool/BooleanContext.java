package cz.muni.fi.editor.cmisserver.query.dsl.impl.bool;

import cz.muni.fi.editor.cmisserver.query.dsl.BooleanMustContext;
import cz.muni.fi.editor.cmisserver.query.dsl.BooleanStartContext;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public class BooleanContext implements BooleanMustContext
{
    private List<BooleanClause> clauses = new ArrayList<>();


    @Override
    public BooleanStartContext should(Query query)
    {
        clauses.add(new BooleanClause(query, BooleanClause.Occur.SHOULD));

        return this;
    }

    @Override
    public BooleanMustContext must(Query query)
    {
        clauses.add(new BooleanClause(query, BooleanClause.Occur.MUST));

        return this;
    }

    @Override
    public Query createQuery()
    {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        for (BooleanClause bc : clauses)
        {
            builder.add(bc);
        }

        return builder.build();
    }

    @Override
    public BooleanStartContext not()
    {
        int lastIndex = clauses.size() - 1;
        BooleanClause last = clauses.get(lastIndex);
        if (!last.getOccur().equals(BooleanClause.Occur.MUST))
        {
            throw new IllegalStateException("Previous clause is not of 'must' type.");
        }

        clauses.set(lastIndex, new BooleanClause(last.getQuery(), BooleanClause.Occur.MUST_NOT));

        return this;
    }
}
