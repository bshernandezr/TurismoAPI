package com.turismo.utils;

import lombok.Data;

/**
 * Object for send the response message when a REST service is called.
 * 
 * @author Brayan Hernandez
 *
 */
@Data
public class ResponseMsg {

	private String msg;

	public ResponseMsg(String msg) {
		this.msg = msg;
	}

}
