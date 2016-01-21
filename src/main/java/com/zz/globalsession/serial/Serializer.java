package com.zz.globalsession.serial;
public interface Serializer {

	/**
	 * Serialize the given object to binary data.
	 * @param <T>
	 * 
	 * @param t object to serialize
	 * @return the equivalent binary data
	 */
	<T> byte[] serialize(T t) ;

	/**
	 * Deserialize an object from the given binary data.
	 * @param <T>
	 * 
	 * @param bytes object binary representation
	 * @return the equivalent object instance
	 */
	<T> T deserialize(byte[] bytes) ;
}