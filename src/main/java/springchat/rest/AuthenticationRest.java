/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package springchat.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springchat.data.dto.AuthenticationResultDto;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationRest {

    @Autowired
    @Qualifier("authenticationManager")
    protected AuthenticationManager authenticationManager;

    @RequestMapping(value = "/rest/auth", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public AuthenticationResultDto postUser(@RequestParam("user") String user, HttpServletRequest request) {
        AuthenticationResultDto dto = new AuthenticationResultDto();
        dto.setSessionId(request.getSession().getId());
        try {
            // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
            AbstractAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, "");
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            dto.setSuccess(Boolean.TRUE);
            request.getSession().setAttribute("authenticated", Boolean.TRUE);
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            dto.setSuccess(Boolean.FALSE);
            request.getSession().setAttribute("authenticated", Boolean.FALSE);
        }
        return dto;
    }

    @RequestMapping(value = "/rest/auth", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void ping() {
        // no-op
    }

}
