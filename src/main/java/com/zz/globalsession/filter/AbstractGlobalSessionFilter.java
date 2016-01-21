/*
 * Copyright 2012 M3, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.zz.globalsession.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zz.globalsession.GlobalHttpSession;
import com.zz.globalsession.GlobalSessionFilterSettings;
import com.zz.globalsession.GlobalSessionHttpRequest;
import com.zz.globalsession.store.SessionStore;
import com.zz.globalsession.util.CookieUtil;

public abstract class AbstractGlobalSessionFilter implements Filter{

    private static Log          log                     = LogFactory.getLog(AbstractGlobalSessionFilter.class);

    private static final String GLOBAL_NAMESPACE        = "GLOBAL";
    private static final String DEFAULT_SESSION_ID_NAME = "__gsid__";

/*    private static class ConfigKey {

        public static final String NAMESPACE       = "namespace";
        public static final String SESSION_ID      = "sessionId";
        public static final String DOMAIN          = "domain";
        public static final String PATH            = "path";
        public static final String SECURE          = "secure";
        public static final String HTTP_ONLY       = "httpOnly";
        public static final String SESSION_TIMEOUT = "sessionTimeout";
        public static final String EXCLUDE_REG_EXP = "excludeRegExp";
    }*/

    private static class RequestAttributeKey {

        protected static final String SESSION_STATUS = "__sessionStatus__";
    }

   private static enum SessionStatus {
        unknown, fixed
    }

    protected SessionStore                store;
    protected GlobalSessionFilterSettings settings;

//    private GlobalSessionFilterSettings getGlobalSessionFilterSettings(FilterConfig config) {
//
//        GlobalSessionFilterSettings settings = new GlobalSessionFilterSettings();
//
//        settings.setNamespace(getConfigValue(config, ConfigKey.NAMESPACE));
//        if (settings.getNamespace() == null) {
//            settings.setNamespace(GLOBAL_NAMESPACE);
//        }
//
//        settings.setExcludeRegExp(getConfigValue(config, ConfigKey.EXCLUDE_REG_EXP));
//
//        settings.setSessionIdKey(getConfigValue(config, ConfigKey.SESSION_ID));
//        if (settings.getSessionIdKey() == null) {
//            settings.setSessionIdKey(DEFAULT_SESSION_ID_NAME);
//        }
//
//        settings.setDomain(getConfigValue(config, ConfigKey.DOMAIN));
//
//        settings.setPath(getConfigValue(config, ConfigKey.PATH));
//        if (settings.getPath() == null) {
//            settings.setPath("/");
//        }
//
//        settings.setSecure(getConfigValue(config, ConfigKey.SECURE) != null
//                           && getConfigValue(config, ConfigKey.SECURE).equals("true"));
//
//        settings.setHttpOnly(getConfigValue(config, ConfigKey.HTTP_ONLY) != null
//                             && getConfigValue(config, ConfigKey.HTTP_ONLY).equals("true"));
//
//        String sessionTimeout = getConfigValue(config, ConfigKey.SESSION_TIMEOUT);
//        if (sessionTimeout == null) {
//            settings.setSessionTimeoutMinutes(10);
//        } else {
//            settings.setSessionTimeoutMinutes(Integer.valueOf(sessionTimeout));
//        }
//
//        return settings;
//    }

    private Cookie getCurrentValidSessionIdCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals(settings.getSessionIdKey()) && cookie.getValue() != null
                    && cookie.getValue().trim().length() > 0) {
                    if (isValidSession(createGlobalSessionRequest(req, cookie.getValue()))) {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie is found. (" + settings.getSessionIdKey() + " -> "
                                      + cookie.getValue() + ")");
                        }
                        return cookie;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie is found but it's invalid. (" + settings.getSessionIdKey()
                                      + " -> " + cookie.getValue() + ")");
                        }
                        continue;
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("SessionId cookie is not found.");
        }
        return null;
    }

    private Cookie generateSessionIdCookie(String sessionIdValue) {

        Cookie sessionIdCookie = new Cookie(settings.getSessionIdKey(), sessionIdValue);
        if (settings.getDomain() != null) {
            sessionIdCookie.setDomain(settings.getDomain());
        }
        if (settings.getPath() != null) {
            sessionIdCookie.setPath(settings.getPath());
        } else {
            sessionIdCookie.setPath("/");
        }
        if(settings.isSecure())
        sessionIdCookie.setSecure(settings.isSecure());
        // [Note] httpOnly is not supported by Servlet API 2.x, so add it
        // manually later.
        return sessionIdCookie;
    }

    private GlobalSessionHttpRequest createGlobalSessionRequest(HttpServletRequest req, String sessionIdValue) {
        return new GlobalSessionHttpRequest(req,
            sessionIdValue,
            settings.getNamespace(),
            settings.getSessionTimeoutMinutes(),
            store);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }


    public void initSettings(){
    	if (settings == null) {
            settings = new GlobalSessionFilterSettings();
        }
        // 以下内空为新增的，目的为了利用DelegatingFilterProxy 配置filter
        // private String
        // namespace,sessionId,domain,path,secure,httpOnly,sessiontTimeout,excludeRegExp;
        if (namespace != null) {
            settings.setNamespace(namespace);
        }else{
        	settings.setNamespace(GLOBAL_NAMESPACE);
        }
        if (sessionId != null) {
            settings.setSessionIdKey(sessionId);
        }else{
        	settings.setSessionIdKey(DEFAULT_SESSION_ID_NAME);
        }
        if (domain != null) {
            settings.setDomain(domain);
        }
        if (path != null) {
            settings.setPath(path);
        }
        if (excludeRegExp != null) {
            settings.setExcludeRegExp(excludeRegExp);
        }
        if (sessiontTimeout > 0) {
            settings.setSessionTimeoutMinutes(sessiontTimeout);
        }
        if (httpOnly) {
            settings.setHttpOnly(httpOnly);
        }
        if (secure) {
            settings.setSecure(secure);
        }
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
                                                                                    ServletException {

        HttpServletRequest _req = (HttpServletRequest) req;
        HttpServletResponse _res = (HttpServletResponse) res;

        if (isGlobalSessionHttpRequest(_req)) {

            if (log.isDebugEnabled()) {
                log.debug("GlobalSessionHttpRequest is already applied.");
            }
            chain.doFilter(_req, _res);

        } else if (settings.getExcludeRegExp() != null && _req.getRequestURI().matches(settings.getExcludeRegExp())) {

            if (log.isDebugEnabled()) {
                log.debug("This URI is excluded. (URI: " + _req.getRequestURI() + ")");
            }
            chain.doFilter(_req, _res);

        } else {

            Cookie currentValidSessionIdCookie = getCurrentValidSessionIdCookie(_req);

            String sessionIdValue = null;
            if (currentValidSessionIdCookie == null) {
                // copy JSESSIONID value to original session
                sessionIdValue = _req.getSession().getId();
            } else {
                // current original session is valid
                sessionIdValue = currentValidSessionIdCookie.getValue();
            }

            if (currentValidSessionIdCookie == null) {
                Cookie newSessionIdCookie = generateSessionIdCookie(sessionIdValue);
                // [Note] httpOnly is not supported by Servlet API 2.x, so need
                // to call #addHeader instead of #addCookie
                /*  */
                String setCookie = CookieUtil.createSetCookieHeaderValue(newSessionIdCookie, settings.isHttpOnly());
                _res.addHeader("Set-Cookie", setCookie);
               
               // _res.addCookie(newSessionIdCookie);
                setSessionStatus(_req, SessionStatus.fixed);
           
                if (log.isDebugEnabled()) {
                    log.debug("SessionId cookie is updated. (" + sessionIdValue + ")");
                }
            }

            // doFilter with the request wrapper
            GlobalSessionHttpRequest _wrappedReq = createGlobalSessionRequest(_req, sessionIdValue);
            chain.doFilter(_wrappedReq, _res);

            // update attributes, expiration
            GlobalHttpSession session = _wrappedReq.getSession();
            session.reloadAttributes(); // need reloading from the store to work
                                        // with GlassFish
            session.save();
        }
    }

    @Override
    public void destroy() {
    }

    protected static String getConfigValue(FilterConfig config, String keyName) {
        String fromInitParam = config.getInitParameter(keyName);
        if (fromInitParam != null) {
            return fromInitParam;
        }
        return System.getProperty(keyName);
    }

    private void setSessionStatus(HttpServletRequest req, SessionStatus status) {
        req.setAttribute(RequestAttributeKey.SESSION_STATUS, status);
    }

    private SessionStatus getSessionStatus(HttpServletRequest req) {
        Object status = req.getAttribute(RequestAttributeKey.SESSION_STATUS);
        if (status == null) {
            return SessionStatus.unknown;
        } else {
            return (SessionStatus) status;
        }
    }

    private boolean isValidSession(GlobalSessionHttpRequest req) {
        if (getSessionStatus(req) == SessionStatus.fixed) {
            return true;
        }
        return req.getSession().isValid();
    }

    private boolean isGlobalSessionHttpRequest(HttpServletRequest req) {
        return req.getSession() instanceof GlobalHttpSession;
    }

    // 新增属性
    private String namespace, sessionId, domain, path;
    private boolean httpOnly, secure;
    private int     sessiontTimeout;
    private String  excludeRegExp;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
// cookie name 
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setSessiontTimeout(int sessiontTimeout) {
        this.sessiontTimeout = sessiontTimeout;
    }

    public void setExcludeRegExp(String excludeRegExp) {
        this.excludeRegExp = excludeRegExp;
    }

    
 
    
   

}
