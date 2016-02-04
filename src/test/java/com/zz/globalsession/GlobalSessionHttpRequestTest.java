package com.zz.globalsession;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import static org.mockito.BDDMockito.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.zz.globalsession.store.SessionStore;

public class GlobalSessionHttpRequestTest {

    @Test
    public void type() throws Exception {
        assertThat(GlobalSessionHttpRequest.class, notNullValue());
    }

    // TODO currently depends on Servlet API
    @Test(expected = IllegalArgumentException.class)
    public void instantiation() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String sessionId = "xxx";
        String namespace = "namespace";
        SessionStore store = mock(SessionStore.class);
        new GlobalSessionHttpRequest(request, sessionId, namespace, 30, store);
    }

    // TODO currently depends on Servlet API
    @Test(expected = IllegalArgumentException.class)
    public void getSession_A$() throws Exception {
        // given
        HttpServletRequest req = mock(HttpServletRequest.class);
        String sessionId = "xxx";
        String namespace = "namespace";
        SessionStore store = mock(SessionStore.class);
        GlobalSessionHttpRequest request = new GlobalSessionHttpRequest(req, sessionId, namespace, 30, store);
        // when
        GlobalHttpSession actual = request.getSession();
        // then
        assertThat(actual, is(not(nullValue())));
    }

    // TODO currently depends on Servlet API
    @Test(expected = IllegalArgumentException.class)
    public void getSession_A$boolean() throws Exception {
        // given
        HttpServletRequest req = mock(HttpServletRequest.class);
        String sessionId = "xxx";
        String namespace = "namespace";
        SessionStore store = mock(SessionStore.class);
        GlobalSessionHttpRequest request = new GlobalSessionHttpRequest(req, sessionId, namespace, 30, store);
        // when
        boolean create = false;
        GlobalHttpSession actual = request.getSession(create);
        // then
        assertThat(actual, is(not(nullValue())));
    }

}
