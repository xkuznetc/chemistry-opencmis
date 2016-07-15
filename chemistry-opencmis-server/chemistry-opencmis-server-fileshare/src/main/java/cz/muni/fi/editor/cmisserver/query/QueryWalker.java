package cz.muni.fi.editor.cmisserver.query;

import org.antlr.runtime.tree.Tree;
import org.apache.lucene.search.Query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public interface QueryWalker
{
    void handleWhereBranch(Tree tree) throws IllegalArgumentException;
    Query obtainQuery() throws IllegalStateException;
}
