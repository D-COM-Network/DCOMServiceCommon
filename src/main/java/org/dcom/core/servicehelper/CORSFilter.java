/*
Copyright (C) 2022 Cardiff

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/
package org.dcom.core.servicehelper;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
*This is a generic filter class for disabling Cross Origin Restrictions – required for the implementation of REST Web Services. 
*
*/
@Provider
@PreMatching
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Method for ContainerRequestFilter.
     */
    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
            return;
        }
    }

    /**
     * A preflight request is an OPTIONS request
     * with an Origin header.
     */
    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    /**
     * Method for ContainerResponseFilter.
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {

        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
      //  System.out.println(request.getUriInfo().getPath());
        if (request.getHeaderString("Origin") == null) {
            return;
        }

        // If it is a preflight request, then we add all
        // the CORS headers here.

        if (isPreflightRequest(request)) {
            try {
              MultivaluedMap<String,String> headers=request.getHeaders();
              String headerList="";
              boolean firstHeader=true;
              for (String header:headers.keySet()) {
                  List<String> currentParam=headers.get(header);
                  for (String param:currentParam) {
                      if (header.equals("origin")) response.getHeaders().add("Access-Control-Allow-Origin",param);
                      if (header.equals("access-control-request-headers")) {
                        if (firstHeader) firstHeader=false; else headerList=headerList+",";
                        headerList=headerList+param;
                      }
                  }
              }
              response.getHeaders().add("Access-Control-Allow-Credentials", "true");
              response.getHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT");
              response.getHeaders().add("Access-Control-Allow-Headers",headerList);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
            response.getHeaders().add("Access-Control-Allow-Origin", "*");
        }

    }
}
