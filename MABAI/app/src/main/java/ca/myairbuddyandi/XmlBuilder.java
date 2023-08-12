package ca.myairbuddyandi;

import java.io.IOException;

public final class XmlBuilder {

    // Static
    private static final String CLOSE_WITH_TICK = "'>";
    private static final String COL_CLOSE = "</col>";
    private static final String COL_OPEN = "<col name='";
    private static final String DB_CLOSE = "</database>";
    private static final String DB_OPEN = "<database name='";
    private static final String OPEN_XML_STANZA = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static final String ROW_CLOSE = "</row>";
    private static final String ROW_OPEN = "<row>";
    private static final String TABLE_CLOSE = "</table>";
    private static final String TABLE_OPEN = "<table name='";

    // Public

    // Protected

    // Private
    private final StringBuilder sb;

    // End of variables

    // Public constructor
    public XmlBuilder() throws IOException {
        sb = new StringBuilder();
    }

    void start(final String dbName) {
        sb.append(OPEN_XML_STANZA);
        sb.append(DB_OPEN).append(dbName).append(CLOSE_WITH_TICK);
    }

    String end() throws IOException {
        sb.append(DB_CLOSE);
        return sb.toString();
    }

    void openTable(final String tableName) {
        sb.append(TABLE_OPEN).append(tableName).append(CLOSE_WITH_TICK);
    }

    void closeTable() {
        sb.append(TABLE_CLOSE);
    }

    void openRow() {
        sb.append(ROW_OPEN);
    }

    void closeRow() {
        sb.append(ROW_CLOSE);
    }

    void addColumn(final String name, final String val) {
        sb.append(COL_OPEN).append(name).append(CLOSE_WITH_TICK).append(val).append(COL_CLOSE);
    }
}
