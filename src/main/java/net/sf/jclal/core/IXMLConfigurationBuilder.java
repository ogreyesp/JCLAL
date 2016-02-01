/*
 * Copyright (C) 2014
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
package net.sf.jclal.core;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;

/**
 * Interface for the XML Configuration Builder.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public interface IXMLConfigurationBuilder {

    /**
     * Write the XML file.
     *
     * @throws ConfigurationException The exception that will be launched.
     */
    public void writeXmlFile() throws ConfigurationException;

    /**
     * Write the XML file
     * 
     * @param newXml New file destination of the xml configuration.
     * @param replace If the file exists, replace with the new one.
     * @throws ConfigurationException The exception that will be launched.
     */
    public void writeXmlFile(File newXml, boolean replace) throws ConfigurationException, Exception;
}
