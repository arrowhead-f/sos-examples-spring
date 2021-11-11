package eu.arrowhead.application.skeleton.subscriber.security;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberDefaults;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.filter.ArrowheadFilter;

public class SubscriberNotificationAccessControlFilter extends ArrowheadFilter {

	//=================================================================================================
	// members
	
	private static final CoreSystem[] allowedCoreSystemsForSendingNotification = { CoreSystem.EVENTHANDLER };
	
	private Map<String, String> eventTypeMap;
	private String serverCN;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void setEventTypeMap( final Map<String, String> eventTypeMap) { this.eventTypeMap = eventTypeMap;}
	public void setServerCN( final String serverCN) { this.serverCN = serverCN; }

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			log.debug("Checking access in SubscriberNotificationAccessControlFilter...");
			try {
				final HttpServletRequest httpRequest = (HttpServletRequest) request;
				final String requestTarget = Utilities.stripEndSlash(httpRequest.getRequestURL().toString());
					
				if (eventTypeMap != null) {
					for (final String notificationUri  : eventTypeMap.values()) {
						if (requestTarget.endsWith(SubscriberDefaults.DEFAULT_EVENT_NOTIFICATION_BASE_URI + "/" + notificationUri)) {
							checkIfClientIsAnAllowedCoreSystem(getCertificateCNFromRequest(httpRequest), getServerCloudCN(serverCN), allowedCoreSystemsForSendingNotification, requestTarget);
						}
					}
				}
			} catch (final ArrowheadException ex) {
				handleException(ex, response);
			}
		}
		
		chain.doFilter(request, response);
	}
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	@Nullable
	private String getCertificateCNFromRequest(final HttpServletRequest request) {
		final X509Certificate[] certificates = (X509Certificate[]) request.getAttribute(CommonConstants.ATTR_JAVAX_SERVLET_REQUEST_X509_CERTIFICATE);
		if (certificates != null && certificates.length != 0) {
			final X509Certificate cert = certificates[0];
			return Utilities.getCertCNFromSubject(cert.getSubjectDN().getName());
		}
		
		return null;
	}
	
	//-------------------------------------------------------------------------------------------------
	protected void checkIfClientIsAnAllowedCoreSystem(final String clientCN, final String cloudCN, final CoreSystem[] allowedCoreSystems, final String requestTarget) {
		final boolean checkResult = checkIfClientIsAnAllowedCoreSystemNoException(clientCN, cloudCN, allowedCoreSystems, requestTarget);

		if (!checkResult) {
			// client is not an allowed core system
			log.debug("Only dedicated core systems can use {}, access denied!", requestTarget);
			throw new AuthException(clientCN + " is unauthorized to access " + requestTarget);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	protected boolean checkIfClientIsAnAllowedCoreSystemNoException(final String clientCN, final String cloudCN, final CoreSystem[] allowedCoreSystems, final String requestTarget) {
		for (final CoreSystem coreSystem : allowedCoreSystems) {
			final String coreSystemCN = coreSystem.name().toLowerCase() + "." + cloudCN;
			if (clientCN.equalsIgnoreCase(coreSystemCN)) {
				return true;
			}
		}
		
		return false;
	}
	
	//-------------------------------------------------------------------------------------------------
	protected String getServerCloudCN( final String serverCN ) {
	    
		final String[] serverFields = serverCN.split("\\.", 2); // serverFields contains: coreSystemName, cloudName.operator.arrowhead.eu
	    Assert.isTrue(serverFields.length >= 2, "Server common name is invalid: " + serverCN);
	    
	    return serverFields[1];
	}
}