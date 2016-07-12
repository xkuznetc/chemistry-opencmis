package cz.muni.fi.editor.cmisserver;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static class LuceneServiceImpl implements LuceneService
    {
        private Directory directory;
        private IndexWriter writer;
        private DirectoryReader reader;

        public LuceneServiceImpl(Path indexLocation) throws IOException
        {
            directory = FSDirectory.open(indexLocation);
            IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            writer = new IndexWriter(directory, iwc);
            reader = DirectoryReader.open(writer);
        }

        @Override
        public void add(Document document)
        {
            try
            {
                writer.addDocument(document);
            }
            catch (IOException ex)
            {
                LOG.error(ex.getMessage());
            }
        }

        @Override
        public void add(List<Document> documents)
        {

        }

        @Override
        public List<String> search(Query query)
        {
            Boolean isCurrent = null;
            try
            {
                isCurrent = reader.isCurrent();
            }
            catch (IOException ex)
            {
                LOG.error(ex.getMessage());
            }

            if (isCurrent != null)
            {
                List<String> result = new ArrayList<>();
                IndexSearcher search = null;
                if (isCurrent.equals(Boolean.TRUE))
                {
                    // do search
                    search = new IndexSearcher(reader);
                }
                else
                {
                    DirectoryReader newReader = null;
                    try
                    {
                        newReader = DirectoryReader.open(writer, true, true);
                        reader.close();
                        reader = newReader;
                    }
                    catch (IOException ex)
                    {
                        LOG.error(ex.getMessage());
                    }

                    search = new IndexSearcher(reader);
                }

                try
                {
                    TopDocs top = search.search(query, 15);

                    for (ScoreDoc sd : top.scoreDocs)
                    {
                        result.add(search.doc(sd.doc).get("id"));
                    }
                }
                catch (IOException ex)
                {
                    LOG.error(ex.getMessage());
                }

                return result;

            }
            else
            {
                return Collections.emptyList();
            }
        }

        @Override
        public void close() throws IOException
        {
            writer.close();
            reader.close();
        }

        @Override
        public void delete(Document document)
        {

        }
    }
}
