package cz.muni.fi.editor.cmisserver;

import org.antlr.runtime.RecognitionException;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public class QueryParser
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryParser.class);

    public ObjectList query(TypeManager tm, String user, String repositoryId,
                            String statement, Boolean searchAllVersions, Boolean includeAllowableActions,
                            IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems, BigInteger skipCount)
    {

        QueryUtilStrict queryUtilStrict = new QueryUtilStrict(statement, tm, null);
        QueryObject queryObject = null;

        boolean error = false;
        try
        {
            queryUtilStrict.processStatement();
            LOG.info("PARSED");
            queryObject = queryUtilStrict.getQueryObject();
        }
        catch (RecognitionException e)
        {
            error = true;
            LOG.error(e.getMessage());
        }


        if (!error)
        {
            List<CmisSelector> select = queryObject.getSelectReferences();
            Map<String, String> from = queryObject.getTypes();
            List<CmisSelector> where = queryObject.getWhereReferences();
            List<QueryObject.SortSpec> orderBy = queryObject.getOrderBys();

            parseSelect(select);
            parseFrom(from);
            parseWhere(where);
            parseOrderBy(orderBy);

        }

        ObjectListImpl result = new ObjectListImpl();
        result.setNumItems(BigInteger.ZERO);
        result.setHasMoreItems(Boolean.FALSE);
        result.setObjects(new ArrayList<ObjectData>());

        for(Integer i : queryObject.getColumnReferences().keySet()){
            LOG.error(queryObject.getTypeReference(i));

        }

        return result;
    }

    private void parseSelect(List<CmisSelector> select)
    {
        LOG.info("SELECT");

        for (CmisSelector property : select)
        {
            LOG.info(" WHAT: " + property.getName());
            LOG.info(" [Alias: " + property.getAliasName() + " ]");

            if (property instanceof ColumnReference)
            {
                ColumnReference colRef = (ColumnReference) property;
//                LOG.info("(TypeDef) " + colRef.getTypeDefinition());
                LOG.info("IsColRef");
            }
            else if (property instanceof FunctionReference)
            {
                LOG.info("Function reference");
            }
        }
    }

    private void parseFrom(Map<String, String> from)
    {
        LOG.info("FROM:");
        for (Map.Entry<String, String> f : from.entrySet())
        {
            LOG.info("  Queryname: " + f.getValue());
            LOG.info(" [a: " + f.getKey() + " ]");
        }
    }

    private void parseWhere(List<CmisSelector> where)
    {
        LOG.info("WHERE: ");
        for (CmisSelector cs : where)
        {
            ColumnReference cr = (ColumnReference) cs;
            LOG.info("  q {}",cr.getQualifier());
        }
    }

    private void parseOrderBy(List<QueryObject.SortSpec> orderBys)
    {
        LOG.info("ORDER BY :");
        if (orderBys == null || orderBys.isEmpty())
        {
            LOG.info("none");
        }
        else
        {
            for (QueryObject.SortSpec ss : orderBys)
            {
                LOG.info("n: {}, {}", ss.getSelector().getName(),ss.isAscending() ? "ASC" : "DESC");
            }
        }
    }

}

