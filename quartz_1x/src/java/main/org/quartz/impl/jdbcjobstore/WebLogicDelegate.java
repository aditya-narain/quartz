/*
 * Copyright (c) 2004-2005 by OpenSymphony
 * All rights reserved.
 * 
 * Previously Copyright (c) 2001-2004 James House
 */
package org.quartz.impl.jdbcjobstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

/**
 * <p>
 * This is a driver delegate for the WebLogic JDBC driver.
 * </p>
 * 
 * @see org.quartz.impl.jdbcjobstore.oracle.WebLogicOracleDelegate
 * @author <a href="mailto:jeff@binaryfeed.org">Jeffrey Wescott</a>
 */
public class WebLogicDelegate extends StdJDBCDelegate {
    /**
     * <p>
     * Create new WebLogicDelegate instance.
     * </p>
     * 
     * @param logger
     *          the logger to use during execution
     * @param tablePrefix
     *          the prefix of all table names
     */
    public WebLogicDelegate(Log log, String tablePrefix, String instanceId) {
        super(log, tablePrefix, instanceId);
    }

    /**
     * <p>
     * Create new WebLogicDelegate instance.
     * </p>
     * 
     * @param logger
     *          the logger to use during execution
     * @param tablePrefix
     *          the prefix of all table names
     * @param useProperties
     *          use java.util.Properties for storage
     */
    public WebLogicDelegate(Log log, String tablePrefix, String instanceId,
            Boolean useProperties) {
        super(log, tablePrefix, instanceId, useProperties);
    }

    //---------------------------------------------------------------------------
    // protected methods that can be overridden by subclasses
    //---------------------------------------------------------------------------

    /**
     * <p>
     * This method should be overridden by any delegate subclasses that need
     * special handling for BLOBs. The default implementation uses standard
     * JDBC <code>java.sql.Blob</code> operations.
     * </p>
     * 
     * @param rs
     *          the result set, already queued to the correct row
     * @param colName
     *          the column name for the BLOB
     * @return the deserialized Object from the ResultSet BLOB
     * @throws ClassNotFoundException
     *           if a class found during deserialization cannot be found
     * @throws IOException
     *           if deserialization causes an error
     */
    protected Object getObjectFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
        Object obj = null;

        Blob blobLocator = rs.getBlob(colName);
        InputStream binaryInput = null;
        try {
            if (null != blobLocator && blobLocator.length() > 0) {
                binaryInput = blobLocator.getBinaryStream();
            }
        } catch (Exception ignore) {
        }

        if (null != binaryInput) {
            ObjectInputStream in = new ObjectInputStream(binaryInput);
            obj = in.readObject();
            in.close();
        }

        return obj;
    }

    protected Object getJobDetailFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
        if (canUseProperties()) {
            Blob blobLocator = rs.getBlob(colName);
            InputStream binaryInput = null;
            try {
                if (null != blobLocator && blobLocator.length() > 0) {
                    binaryInput = blobLocator.getBinaryStream();
                }
            } catch (Exception ignore) {
            }
            return binaryInput;
        }

        return getObjectFromBlob(rs, colName);
    }
}

// EOF