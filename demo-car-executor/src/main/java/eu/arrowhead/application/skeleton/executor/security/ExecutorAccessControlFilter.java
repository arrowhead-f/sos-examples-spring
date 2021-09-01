package eu.arrowhead.application.skeleton.executor.security;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.security.AccessControlFilter;

@Component
@ConditionalOnProperty(name = CommonConstants.SERVER_SSL_ENABLED, matchIfMissing = true)
public class ExecutorAccessControlFilter extends AccessControlFilter {

	//=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
	@Override
	protected void checkClientAuthorized(final String clientCN, final String method, final String requestTarget, final String requestJSON, final Map<String,String[]> queryParams) {
		super.checkClientAuthorized(clientCN, method, requestTarget, requestJSON, queryParams);
		
		final String cloudCN = getServerCloudCN();
		
		if (requestTarget.contains(CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_START_URI) ||
			requestTarget.contains(CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_ABORT_URI) ||
			requestTarget.contains(CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_INFO_URI)) {
			//Only Choreographer Core System is allowed to call this endpoints
			checkIfClientIsChoreographer(clientCN, cloudCN);			
		}
	}
	
	//=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
	private void checkIfClientIsChoreographer(final String clientCN, final String cloudCN) {
		final String coreSystemCN = CoreSystem.CHOREOGRAPHER.name().toLowerCase() + "." + cloudCN;
		if (!clientCN.equalsIgnoreCase(coreSystemCN)) {
			throw new AuthException("Only Choreographer Core System is allowed to call this endpoint", HttpStatus.UNAUTHORIZED.value());
		}
	}
}
