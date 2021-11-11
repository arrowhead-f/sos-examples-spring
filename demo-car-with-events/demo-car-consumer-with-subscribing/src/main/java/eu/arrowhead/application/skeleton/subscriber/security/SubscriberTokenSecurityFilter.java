package eu.arrowhead.application.skeleton.subscriber.security;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;

import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberDefaults;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.token.TokenSecurityFilter;

public class SubscriberTokenSecurityFilter extends TokenSecurityFilter {
	
	//=================================================================================================
	// members
	
	private PrivateKey myPrivateKey;
	private PublicKey authorizationPublicKey;
	
	private Map<String, String> eventTypeMap;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected PrivateKey getMyPrivateKey() {
		return myPrivateKey;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected PublicKey getAuthorizationPublicKey() {
		return authorizationPublicKey;
	}

	//-------------------------------------------------------------------------------------------------
	public void setMyPrivateKey(final PrivateKey myPrivateKey) {
		this.myPrivateKey = myPrivateKey;
	}

	//-------------------------------------------------------------------------------------------------
	public void setAuthorizationPublicKey(final PublicKey authorizationPublicKey) {
		this.authorizationPublicKey = authorizationPublicKey;
	}
	
	//-------------------------------------------------------------------------------------------------	
	public void setEventTypeMap(final Map<String, String> eventTypeMap) {
		this.eventTypeMap = eventTypeMap;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			log.debug("Checking access in TokenSecurityFilter...");
			try {
				final HttpServletRequest httpRequest = (HttpServletRequest) request;
				final String requestTarget = Utilities.stripEndSlash(httpRequest.getRequestURL().toString());
				
				if (eventTypeMap != null) {
					for (final String notificationUri  : eventTypeMap.values()) {
						if (requestTarget.endsWith( SubscriberDefaults.DEFAULT_EVENT_NOTIFICATION_BASE_URI + "/" + notificationUri)) {
							chain.doFilter(request, response);
							return;
						}
					}
				}
	
				final String clientCN = getCertificateCNFromRequest(httpRequest);
				if (clientCN == null) {
					log.error("Unauthorized access: {}", requestTarget);
					throw new AuthException("Unauthorized access: " + requestTarget);
				}
				
				final String token = httpRequest.getParameter(CommonConstants.REQUEST_PARAM_TOKEN);
				if (Utilities.isEmpty(token)) {
					log.error("Unauthorized access: {}, no token is specified", requestTarget);
					throw new AuthException("Unauthorized access: " + requestTarget + ", no token is specified");
				}
				
				checkToken(clientCN, token, requestTarget);
			} catch (final ArrowheadException ex) {
				handleException(ex, response);
			}
		}
		
		chain.doFilter(request, response);
	}
	
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
}