package cz.muni.fi.editor.cmisserver.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
@Component
public class LuceneServiceFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(LuceneServiceFactory.class);
    private Map<Path, LuceneService> luceneServices = new HashMap<>();

    public LuceneService getLuceneService(Path path) throws IOException
    {
        if (!luceneServices.containsKey(path))
        {
            LuceneService luceneService = new LuceneServiceImpl(path);
            luceneServices.put(path, luceneService);

            return luceneService;
        }
        else
        {
            return luceneServices.get(path);
        }
    }

    @PreDestroy
    public void destroy() throws IOException
    {
        for (LuceneService lc : luceneServices.values())
        {
            lc.close();
        }
    }
}
