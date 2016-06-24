package Leo;

public class HelloWorld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String result = "this is from node " ;
		if(args.length > 0){
			result += args[0];
		}		
		System.out.println(result);
	}
}
