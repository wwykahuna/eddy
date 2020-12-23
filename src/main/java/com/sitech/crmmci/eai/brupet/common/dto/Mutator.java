package com.sitech.crmmci.eai.brupet.common.dto;

public interface Mutator {

	void set(String aKey, Object aValue);

	void setIfNull(String aKey, Object aValue);

	long increment(String aKey);

}