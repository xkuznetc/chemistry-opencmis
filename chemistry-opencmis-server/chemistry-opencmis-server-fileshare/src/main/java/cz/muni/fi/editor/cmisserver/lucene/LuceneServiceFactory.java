package cz.muni.fi.editor.cmisserver.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public class LuceneServiceFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(LuceneServiceFactory.class);
    private static LuceneService luceneService = null;

    private LuceneServiceFactory()
    {
    }

    public static LuceneService getInstance(Path indexLocation) throws IOException
    {
        if (luceneService == null)
        {
            luceneService = new LuceneServiceImpl(indexLocation);
        }

        return luceneService;
    }
}
