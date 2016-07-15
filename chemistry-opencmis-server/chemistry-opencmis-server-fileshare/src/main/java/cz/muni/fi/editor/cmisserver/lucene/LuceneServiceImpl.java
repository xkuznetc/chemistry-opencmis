package cz.muni.fi.editor.cmisserver.lucene;

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
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public class LuceneServiceImpl implements LuceneService
{
    private static final Logger LOG = LoggerFactory.getLogger(LuceneServiceImpl.class);
    private Directory directory;
    private IndexWriter writer;
    private DirectoryReader reader;

    protected LuceneServiceImpl(Path indexLocation) throws IOException
    {
        directory = FSDirectory.open(indexLocation);
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        writer = new IndexWriter(directory, iwc);
        reader = DirectoryReader.open(writer);
    }

    private LuceneServiceImpl()
    {
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
        LOG.info("Obtained query:- {}", query);
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
            LOG.info("current is not null.");
            List<String> result = new ArrayList<>();
            IndexSearcher search = null;
            if (isCurrent.equals(Boolean.TRUE))
            {
                LOG.info("current is current");
                // do search
                search = new IndexSearcher(reader);
                LOG.info("new searcher asigned");
            }
            else
            {
                LOG.info("create new reader");
                DirectoryReader newReader = null;
                try
                {
                    newReader = DirectoryReader.open(writer, true, true);
                    LOG.info("reader created");
                    reader.close();
                    LOG.info("old reader closed");
                    reader = newReader;
                    LOG.info("new reader assigned");
                }
                catch (IOException ex)
                {
                    LOG.error(ex.getMessage());
                }

                search = new IndexSearcher(reader);
                LOG.info("new searcher asigned");
            }

            try
            {
                LOG.info("Explain:");
                LOG.info(search.explain(query,10).toString());
                TopDocs top = search.search(query, 15);
                LOG.info("After search with hits {}",top.totalHits);

                for (ScoreDoc sd : top.scoreDocs)
                {
                    LOG.info("Result:- {} ", sd);
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
            LOG.info("current is null empty list is reulst");
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
