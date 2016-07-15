package cz.muni.fi.editor.cmisserver.query.dsl.impl.keyword;

import cz.muni.fi.editor.cmisserver.query.dsl.KeywordEndContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class KeywordEndContextImpl implements KeywordEndContext
{
    private String[] fields;
    private Object value;

    public KeywordEndContextImpl(String[] fields, Object value){
        this.fields = fields;
        this.value = value;
    }

    @Override
    public Query createQuery()
    {
        List<Query> temps = new ArrayList<>(fields.length);

        for(String field : fields)
        {
            temps.add(new TermQuery(new Term(field,value.toString())));
        }

        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        for(Query q: temps){
            builder.add(q, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }
}
