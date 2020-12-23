package com.sitech.crmmci.eai.brupet.common.cryption;

import org.springframework.stereotype.Service;

@Service
public class SimpleDBDecryption implements DBDecryption {

	private String user;
	private String pass;

	@Override
	public void init(String enUser, String enPass) {
		this.user = enUser;
		this.pass = enPass;
	}

	@Override
	public String getEncryptName() {
		return "Simple";
	}

	@Override
	public String getDencryptUser() {
		return user;
	}

	@Override
	public String getDencryptPass() {
		return pass;
	}

}