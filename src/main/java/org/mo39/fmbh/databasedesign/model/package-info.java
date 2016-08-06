/**
 * The object model if database.<br>
 * <p>
 * //TODO {@link Table} needs improvement.<br>
 * For now, the Ndx, InfoTable, InfoColumn are independent class hard coded as private static class
 * inside utils where they are needed. But the best way to implement this is to make these classes
 * extend {@link Table}, and add a static factory method to create Table according sql. And provide
 * a non-static inner class Record that belongs to Table and holds the String value. This Record
 * class would provide the process of string value such as
 * {@link org.mo39.fmbh.databasedesign.utils.IOUtils#hasValueAtIndex(String, String, int)} and
 * {@link org.mo39.fmbh.databasedesign.utils.IOUtils#replaceValueAtIndex(String, String, int)}. It
 * should also handles the conversion between string and bytes. After that codes could be more
 * cohesive and decoupled.
 */
package org.mo39.fmbh.databasedesign.model;
