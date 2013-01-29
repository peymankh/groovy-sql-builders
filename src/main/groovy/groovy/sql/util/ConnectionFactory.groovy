//<editor-fold desc="Copyright">
/**
 * Copyright 2012 SNUPPS Ltd, UK; SNUPPS Inc, USA 
 * All rights reserved. 
 * This material may not be reproduced, displayed, modified or distributed without the express prior written permission of the copyright holder.
 */
//</editor-fold>

package groovy.sql.util

import groovy.sql.Sql

/**
 *
 * @author Peyman Khanjan
 */
class ConnectionFactory {
    static final DEFAULT_URL = 'jdbc:h2:~/cityalmanac'
    static final DEFAULT_USERNAME = 'sa'
    static final DEFAULT_PASSWORD = ''
    static final DEFAULT_DRIVER_CLASS_NAME = 'org.h2.Driver'

    def static run(closure) {
        def con

        try {
            con = Sql.newInstance(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_DRIVER_CLASS_NAME)
            closure?.call(con)
        }

        finally {
            con.close()
        }
    }
}