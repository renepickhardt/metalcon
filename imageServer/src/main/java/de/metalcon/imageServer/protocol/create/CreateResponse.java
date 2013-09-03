package de.metalcon.imageServer.protocol.create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class CreateResponse extends Response {

	private final ServletContext context;
	private final JSONObject jsonResponse;

	public CreateResponse(ServletContext context) {
		this.context = context;
		this.jsonResponse = new JSONObject();

	}

	@SuppressWarnings("unchecked")
	public void addNoIdentifierError(String noIdentifier) {
		this.jsonResponse.put(
				ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
				noIdentifier);
	}

	@SuppressWarnings("unchecked")
	public void addNoImageStreamError(String imagestreamMissing) {
		this.jsonResponse.put(ProtocolConstants.Parameters.Create.IMAGESTREAM,
				imagestreamMissing);
	}

	@SuppressWarnings("unchecked")
	public void addNoMetadataError(String imageMetadataMissing) {
		this.jsonResponse.put(ProtocolConstants.Parameters.Create.META_DATA,
				imageMetadataMissing);
	}

	@SuppressWarnings("unchecked")
	public void addAutoRotateFlagMissingError(String autorotateFlagMissing) {
		this.jsonResponse.put(
				ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
				autorotateFlagMissing);
	}

	public JSONObject getResponse() {
		return this.jsonResponse;
	}
}