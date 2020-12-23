package com.sitech.crmmci.eai.brupet.common.service;

public interface DBDecryption {

	void init(String enUser, String enPass);

	String getEncryptName();

	String getDencryptUser();

	String getDencryptPass();
}
