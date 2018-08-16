/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package opca.ejb.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.transform.TransformerFactory;

import client.StatutesRsService;
import service.StatutesService;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 *
 * <p>
 * Example injection on a managed bean field:
 * </p>
 *
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {
    // use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly
    @Produces
    @PersistenceContext(unitName="opee")
    private EntityManager em;

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

	private static final String defaultAddress = "http://localhost:8080/statutesrs/rs/";
	
	@Produces
	public StatutesService getStatutesService() {
		try {
			String s = System.getenv("statutesrsservice");
			URL rsLocation;
			if (s != null)
				rsLocation = new URL(s);
			else
				rsLocation = new URL(defaultAddress);
			int retryCount = 10;
			Exception eLast = null;
			while (retryCount-- > 0) {
				try {
					return new StatutesRsService(rsLocation).getRsService();
				} catch (Exception e) {
					eLast = e; 
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
						throw new RuntimeException(e2);
					}
				}
			}
			throw new IllegalStateException("Unable to connect to StatutesService" + eLast.getMessage());
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}
	}

}
