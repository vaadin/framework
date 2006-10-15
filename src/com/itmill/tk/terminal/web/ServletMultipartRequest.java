/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */
   
package com.itmill.tk.terminal.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/** This class wraps the MultipartRequest class by Jason Pell 
 *  for the Servlet environment.
 * 
 * @author IT Mill Ltd
 * @version @VERSION@
 * @since 3.0
 */
public class ServletMultipartRequest extends MultipartRequest
{
	/** 
	 * Constructor wrapper, unwraps the InputStream, 
	 * content type and content lenght from the servlet request object.
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param strSaveDirectory		The temporary directory to save the file from where they can then be moved to wherever by the
	 * 								calling process.  <b>If you specify <u>null</u> for this parameter, then any files uploaded
	 *								will be silently ignored.</B>
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, String strSaveDirectory) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			strSaveDirectory,
			MultipartRequest.MAX_READ_BYTES);
	}

	/** 
	 * Constructor wrapper, unwraps the InputStream, 
	 * content type and content lenght from the servlet request object.
	 * Also allow to explicitly set the max permissable lenght of the request.
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param strSaveDirectory		The temporary directory to save the file from where they can then be moved to wherever by the
	 * 								calling process.  <b>If you specify <u>null</u> for this parameter, then any files uploaded
	 *								will be silently ignored.</B>
	 * @param intMaxReadBytes		Overrides the MAX_BYTES_READ value, to allow arbitrarily long files.
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, String strSaveDirectory, int intMaxReadBytes) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			strSaveDirectory,
			intMaxReadBytes);
	}

	/** 
	 * Constructor wrapper for loading the request into memory rather than temp-file.
	 *
	 * @param request				The HttpServletRequest will be used to initialise the MultipartRequest super class.
	 * @param intMaxReadBytes		Overrides the MAX_BYTES_READ value, to allow arbitrarily long files.
	 *
	 * @exception IllegalArgumentException 	If the request.getContentType() does not contain a Content-Type of "multipart/form-data" or the boundary is not found.
	 * @exception IOException				If the request.getContentLength() is higher than MAX_READ_BYTES or strSaveDirectory is invalid or cannot be written to.
	 *
	 * @see MultipartRequest#MAX_READ_BYTES
	 */
	public ServletMultipartRequest(HttpServletRequest request, int intMaxReadBytes) throws IllegalArgumentException, IOException
	{
 	    super(null, 
			request.getContentType(), 
			request.getContentLength(),
			request.getInputStream(), 
			intMaxReadBytes);
	}
}
