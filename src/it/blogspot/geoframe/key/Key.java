/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.blogspot.geoframe.key;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import net.jcip.annotations.Immutable;

/**
 * @brief Key used as ID for the nodes of the tree
 *
 * @description The Key class is <em>ThreadSafe</em> because
 *              <em>Immutable</em>. The key is stored in hexadecimal format.
 *              Converters <strong>hexToDecimal</strong> and
 *              <strong>decimalToHex</strong> have been implemented as private
 *              methods, thus the user can construct the object providing:
 *              <ul>
 *              <li>a <tt>Key</tt> object;</li>
 *              <li>an <em>hexadecimal</em> String;</li>
 *              <li>a <em>decimal</em> double value.
 *              </ul>
 *              <p>
 *              The <tt>Key</tt> works as key value for <code>Map</code>
 *              objects. The methods <code>equals(Object obj)</code> and
 *              <code>hashCode()</code> have been implemented using the library
 *              <A HREF="http://commons.apache.org/proper/commons-lang/download_lang.cgi">Apache Commons Lang</A>
 *              </p>
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@Immutable
public class Key {

    private final String hexKey; //!< the hexadecimal key

    /**
     * @brief Constructor from a <em>decimal</em> double value
     *
     * @param decimalKey The input value in decimal double format
     */
    public Key (final double decimalKey) {
 
        validateDoubleKey(decimalKey); // precondition
        this.hexKey = decimalToHex(decimalKey);

    }

    /**
     * @overload
     */
    public Key(final String hexKey) {

        validateStringKey(hexKey); // precondition
        this.hexKey = hexKey;

    }

    /**
     * @overload
     */
    public Key (final Key key) {

        validateKey(key); // precondition
        this.hexKey = key.getString();

    }

    /**
     * @brief Getter method key in <strong>hexadecimal</strong> format
     *
     * @return the key in hexadecimal format as <code>String</code> object
     */
    public String getString() {
        return hexKey;
    }

    /**
     * @brief Getter method for key in <strong>decimal</strong> format
     *
     * @return the key in decimal format as <tt>Double</tt> object
     */
    public Double getDouble() {
        return new Double(hexToDecimal());
    }

    /**
     * @brief Compute if the key is odd or even
     *
     * @description The computation is done following these steps:
     *              <ol>
     *              <li>dividing the decimal format of the key by 2;</li>
     *              <li>converting the result from <tt>Double</tt> to
     *              <tt>String</tt>;</li>
     *              <li>parsing the <tt>String</tt> through regular expression,
     *              splitting <strong>integer</strong> part and
     *              <strong>decimal</strong> part and saving them in an array of
     *              <tt>String</tt>;</li>
     *              <li>comparing the <strong>decimal</strong> part with 0.</li>
     *              </ol>
     *
     * @retval TRUE if the key is even
     * @retval FALSE if the key is odd
     */
    public boolean isEven() {

        Double division = hexToDecimal() / 2;
        return isInteger(division);

    }

    /**
     * @brief Indicates wheter some other object is <em>equal to</em> this one
     *
     * @param obj The reference object with which to compare
     * @retval TRUE if this object is the same as the object argument
     * @retval FALSE otherwise
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Key)) return false;
        if (obj == this) return true;

        Key rhs = (Key) obj;
        return new EqualsBuilder().append(hexKey, rhs.hexKey).isEquals();

    }

    /**
     * @brief Returns a hash code value for the object.
     *
     * @description This method is supported for the benefit of hash tables such
     * as those provided by <code>HashMap</code>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 3).append(hexKey).toHashCode();
    }

    public static Key modifiedCantorPairing(final int firstValue, final int secondValue) {

        Double key = 0.5 * (firstValue + secondValue) * (firstValue + secondValue + 1) + (firstValue * secondValue);

        if (isInteger(key)) return new Key(key);
        else {
            String message = "Key cannot be a real number.";
            message += "Modified Cantor Pairing returned a double value.";
            throw new IllegalArgumentException(message);
        }

    }

    private static boolean isInteger(final Double value) {
        return (getFractionalPart(value).compareTo("0") == 0) ? true : false;
    }

    private static String getFractionalPart(final Double value) {
        return splitRealNumberIntoIntegerAndFractional(value)[1];
    }

    private static String getIntegerPart(final Double value) {
        return splitRealNumberIntoIntegerAndFractional(value)[0];
    }

    private static String[] splitRealNumberIntoIntegerAndFractional(final Double value) {
        String tmpString = value.toString();
        return tmpString.split("\\.");
    }

    /**
     * @brief <strong>hexadecimal</strong> to <strong>decimal</strong> format
     *
     * @description The algorithm has been modified from the original version in
     *              <A HREF=
     *              "http://introcs.cs.princeton.edu/java/31datatype/Hex2Decimal.java.html">
     *              Introduction to Programming in Java</A>, in order to work
     *              with <code>double</code> data type
     *
     * @return The decimal value in double
     */
    private double hexToDecimal() {

        final String DIGITS = "0123456789ABCDEF";
        final String HEXKEY = hexKey.toUpperCase();
        double decimalVal = 0.0;

        for (int i = 0; i < HEXKEY.length(); i++) {
            char c = HEXKEY.charAt(i); // parses char by char
            int d = DIGITS.indexOf(c); // checking the corresponding digit
            decimalVal = 16 * decimalVal + d; // conversion
        }

        return decimalVal;

    }

    /**
     * @brief <strong>decimal</strong> to <strong>hexadecimal</strong> format
     *
     * @description The algorithm has been modified from the original version in
     *              <A HREF=
     *              "http://introcs.cs.princeton.edu/java/31datatype/Hex2Decimal.java.html">
     *              Introduction to Programming in Java</A>, in order to work
     *              with <code>double</code> data type
     *
     * @param decimalVal The decimal value to convert in hexadecimal
     * @return The hexadecimal format of the input value
     */
    private String decimalToHex(double decimalVal) {

        final String DIGITS = "0123456789abcdef";

        if (decimalVal == 0.0) return "0";

        String hexadecimal = "";
        while (Math.floor(decimalVal) > 0) {
            int digit = (int) decimalVal % 16;
            hexadecimal = DIGITS.charAt(digit) + hexadecimal;
            decimalVal = decimalVal / 16;
        }

        return hexadecimal;

    }

    /**
     * @brief <strong>Precondition</strong> to validate input hexadecimal string
     *
     * @param hexKey
     *            The input hexadecimal string
     * @exception NumberFormatException
     *                if the input string contains characters that are not
     *                hexadecimal symbols
     */
    private void validateStringKey(final String hexKey) {

        final String DIGITS = "0123456789ABCDEF";
        final String HEXKEY = hexKey.toUpperCase();

        for (int i = 0; i < HEXKEY.length(); i++) {
            char c = HEXKEY.charAt(i);
            int d = DIGITS.indexOf(c);

            if (d < 0) {
                String message = "String '" + hexKey;
                message += "' cannot be converted in";
                message += "hexadecimal format.\n";
                message += c + " is not an hex symbol";
                throw new NumberFormatException(message);
            }

        }

    }

    /**
     * @brief <strong>Precondition</strong> to validate the input Key
     *
     * @param key
     *            The input key
     * @exception NullPointerException
     *                if the input key is <code>null</code>
     */
    private void validateKey(final Key key) {

        if (key == null)
            throw new NullPointerException("The input key is null");

    }

    /**
     * @brief <strong>Precondition</strong> to validate the input decimal value
     *
     * @param doubleKey
     *            The decimal key in input
     * @exception IllegalArgumentException
     *                if the decimal value is negative
     */
    private void validateDoubleKey(final double doubleKey) {

        if (doubleKey < 0) {
            String message = "Negative key - " + doubleKey;
            message += " - are not accepted";
            throw new IllegalArgumentException(message);
        }

    }

}
