package org.deeplearning4j.text.sentenceiterator;

import lombok.NonNull;


import java.io.*;

/**
 * Primitive single-line iterator, without any options involved.
 * Can be used over InputStream or File.
 *
 * Please note: for reset functionality, mark/reset should be supported by underlying InputStream.
 *
 * @author raver119@gmail.com
  */
public class BasicLineIterator implements SentenceIterator {

    private BufferedReader reader;
    private InputStream backendStream;
    private SentencePreProcessor preProcessor;
    private boolean internal = false;

    public BasicLineIterator(@NonNull File file) throws FileNotFoundException {
        this(new FileInputStream(file));
        this.internal = true;
    }

    public BasicLineIterator(@NonNull InputStream stream) {
        this.backendStream = stream;
        reader = new BufferedReader(new InputStreamReader(stream));
    }

    public BasicLineIterator(@NonNull String filePath) throws FileNotFoundException {
        this(new FileInputStream(filePath));
        this.internal = true;
    }

    @Override
    public synchronized String nextSentence() {
        try {
            return (preProcessor != null) ? this.preProcessor.preProcess(reader.readLine()) : reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized boolean hasNext() {
        try {
            return reader.ready();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public synchronized void reset() {
        try {
            if (backendStream instanceof FileInputStream) {
                ((FileInputStream) backendStream).getChannel().position(0);
            } else backendStream.reset();
            reader = new BufferedReader(new InputStreamReader(backendStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finish() {
        try {
            if (this.internal && backendStream != null) backendStream.close();
            if (reader != null) reader.close();
        } catch (Exception e) {
            // do nothing here
        }
    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return preProcessor;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.internal && backendStream != null) backendStream.close();
            if (reader != null) reader.close();
        } catch (Exception e) {
            // do nothing here
            e.printStackTrace();
        }
        super.finalize();
    }
}
