package de.metalcon.utils;

/**
 * This class can serialize and deserialize 64 bit (8 Byte) uuids in alphanumerical strings
 * The class stores the 64 byte string but can also be initialized from a String
 * 
 * This code ist GPLv3
 * 
 * 
 *     	for (int i = 0;i<20;i++){
	    	long tmp = (long) (Math.random()*Long.MAX_VALUE);
	    	UUID s = new UUID(tmp);
	    	UUID s1 = new UUID(s.toString());
	    	System.out.println(tmp + "\t" + s.toString() + "\n" + s1.getUUID() + "\t" + s1.toString() + "\n");
    	}
	    System.exit(0);
 * 
 */

/**
 * @author Rene Pickhardt
 *
 */
public class UUID {
	long uuid;	
	private final static char[] tokens =  {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
	private final static int MAX_TOKEN = 35;

	private final static int[] reverseTokens;
	static {
		reverseTokens = new int[256];
		for (int i = 0;i!=256;++i){
			reverseTokens[i] = -1;
		}
		for (int i = 0; i!= MAX_TOKEN; ++i){
			reverseTokens[(int)(tokens[i])] = i;
		}
	}
	
	
	public UUID(){
		
	}
	
	/**
	 * 
	 * @param id
	 */
	public UUID(long id){
		this.uuid = id;
	}
	/**
	 * 
	 * @param idString
	 */
	public UUID(String idString){
		this.uuid = deserialize(idString);
	}

	/**
	 * 
	 * @return
	 * @Override
	 */
	public String toString(){
		return serialize();
	}
	
	/**
	 * 
	 * @return
	 */
	public long getUUID(){
		return uuid;
	}
	
	private String serialize(){
		long tmp = uuid;
		StringBuilder string = new StringBuilder(13);
		for (int i = 0;i!=13;++i){
			int rest = (int)(tmp%MAX_TOKEN);
			string.append(tokens[rest]);
			tmp = tmp / MAX_TOKEN;
		}
		return string.toString();
	}
	
	/**
	 * @param idString
	 * @return
	 */
	private long deserialize(final String idString) {
		long tmp = 0;
		for (int i = idString.length()-1; i >= 0  ; --i){
			tmp*=MAX_TOKEN;
			char c = idString.charAt(i);
			if (reverseTokens[(int)c]==-1){
				throw new NumberFormatException();
			}
			tmp+=reverseTokens[(int)c];
		}
		return tmp;
	}

	
}