package Entities.Wrappers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class DocumentWrapper extends XWPFDocument implements Serializable {


    public DocumentWrapper(InputStream fis) throws IOException {
        super(fis);
    }


}
