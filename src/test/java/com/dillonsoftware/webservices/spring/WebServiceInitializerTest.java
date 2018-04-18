package com.dillonsoftware.webservices.spring;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.dillonsoftware.webservices.test.MockHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import java.lang.reflect.Field;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceInitializerTest {

	@Mock
	private ServletContext servletContext;

	@Mock
	private ServletRegistration.Dynamic dispatcher;

	@InjectMocks
	private WebServiceInitializer webServiceInitializer;


	@Test
	public void should_initalize_web_service() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		when(servletContext.addServlet(eq("Dispatcher"), any(DispatcherServlet.class))).thenReturn(dispatcher);

		webServiceInitializer.onStartup(servletContext);

		final ArgumentCaptor<ContextLoaderListener> contextLoaderListenerArgumentCaptor = ArgumentCaptor.forClass(ContextLoaderListener.class);
		verify(servletContext).addListener(contextLoaderListenerArgumentCaptor.capture());
		verify(servletContext).addServlet(eq("Dispatcher"), any(DispatcherServlet.class));
		verify(dispatcher).setLoadOnStartup(1);
		verify(dispatcher).addMapping("/");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final ContextLoaderListener contextLoaderListener = contextLoaderListenerArgumentCaptor.getValue();
		final AnnotationConfigWebApplicationContext webApplicationContext = getLocallyDeclatedWebApplicationContext(contextLoaderListener);
		final String[] configLocations = webApplicationContext.getConfigLocations();
		assertEquals(1, configLocations.length);
		assertEquals("com.dillonsoftware.webservices.spring", configLocations[0]);
	}

	private AnnotationConfigWebApplicationContext getLocallyDeclatedWebApplicationContext(
			final ContextLoaderListener contextLoaderListener)
			throws NoSuchFieldException, IllegalAccessException {
		final Field privateField = ContextLoader.class.getDeclaredField("context");
		privateField.setAccessible(true);
		return (AnnotationConfigWebApplicationContext) privateField.get(contextLoaderListener);
	}

}
