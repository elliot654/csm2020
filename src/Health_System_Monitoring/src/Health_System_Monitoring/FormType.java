/**
 * 
 */
package Health_System_Monitoring;

/**
 * @author user
 *
 */
public enum FormType {

		FT_INT {
	        public String toString() {
	            return "Integer";
	        }
		},
		FT_FLOAT {
	        public String toString() {
	            return "Float";
	        }
		},
		FT_STRING {
	        public String toString() {
	            return "String";
	        }
		},
		FT_BOOLEAN {
	        public String toString() {
	            return "Boolean";
	        }
		},
		FT_ERROR {
		public String toString() {
			return "Error";
		}
	};
		
		public static FormType fromString(String type)
		{
			switch(type)
			{
				case("string"):
					return FT_STRING;

				case("integer"):
					return FT_INT;

				case("float"):
					return FT_FLOAT;

				case("boolean"):
					return FT_BOOLEAN;
			}
			return FT_ERROR;
		}
}
