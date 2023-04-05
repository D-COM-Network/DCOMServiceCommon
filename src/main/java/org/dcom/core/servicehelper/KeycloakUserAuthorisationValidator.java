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
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.interfaces.RSAPublicKey;
import com.auth0.jwt.JWTVerifier;
import java.util.Map;
import java.util.ArrayList;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwk.JwkProvider;
import java.net.URL;

/**
*This is the implementation of UserAuthorisationValidator for Keycloak
*
*/
public class KeycloakUserAuthorisationValidator implements UserAuthorisationValidator {
	
	private String url;
	
	public KeycloakUserAuthorisationValidator(String _url) {
			url=_url;
	}
	
	public DecodedJWT getToken(String token) {
		DecodedJWT decodedJwt=null;
		try {
			token=token.replace("Bearer","").trim();
			decodedJwt = JWT.decode(token);
			JwkProvider jwkProvider = new UrlJwkProvider(new URL(url.replace("/auth/","/auth/realms/")+"/protocol/openid-connect/certs"));
			Jwk jwk = jwkProvider.get(decodedJwt.getKeyId());
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
			algorithm.verify(decodedJwt);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return decodedJwt;
		
	}
	
	
	public boolean validateToken(String token) {
			DecodedJWT jwt=getToken(token);
			if (jwt==null) return false;
			return true;
	}
	
	public boolean validatePermission(String token,String role){
		DecodedJWT jwt=getToken(token);
		if (jwt==null) return false;
		Map<String,Object> claim=jwt.getClaim("realm_access").asMap();
		if (claim.containsKey("roles")) {
			ArrayList<Object> c1=(ArrayList<Object>)claim.get("roles");
			for (int i=0; i < c1.size();i++) {
					String val=(String) c1.get(i);
					if (val.equals(role)) {
						return true;
					}
			}
		}
		return false;
			
	}
}