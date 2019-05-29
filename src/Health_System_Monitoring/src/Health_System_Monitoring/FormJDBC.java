package Health_System_Monitoring;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * 
 * @author W!GR
 * This is the JDBC implementation of the Editable Form DAO 
 *
 */
public class FormJDBC implements FormDao {
	
	public Connection database_connection;

	public static FormDao getDAO() { return new FormJDBC(); }

	public FormJDBC()
	{
		database_connection = database_driver.getConnection();
	}

	public int addNewForm(User creator, String form_name)
	{
		 PreparedStatement sqlStatement = null;
		 try {
			 String query = "INSERT INTO forms (form_name,userId) VALUES (?,?)";
			
			 sqlStatement = database_connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			 sqlStatement.setString(1, form_name);
			 sqlStatement.setInt(2, creator.getUserId());
			 
			 sqlStatement.executeUpdate();
			 ResultSet resultSet = sqlStatement.getGeneratedKeys();

			 if (resultSet.next()) {
				 return resultSet.getInt(1);
			 }
			 if (sqlStatement != null) {
                 sqlStatement.close();
             }
		 } catch (SQLException e) {
             e.printStackTrace();
             System.out.println(e.getMessage());

         }
         
         return -1;
	}

	public boolean updateForm(int formId, User creator, String form_name)
	{
		PreparedStatement sqlStatement = null;
        try {
            String query = "UPDATE forms SET form_name = ? , userId = ? WHERE form_id=?";

            sqlStatement = database_connection.prepareStatement(query);

            sqlStatement.setString(1, form_name);
            sqlStatement.setInt(2, creator.getUserId());
            sqlStatement.setInt(3, formId);

            sqlStatement.executeUpdate();
            System.out.println("Form updated");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}

	public boolean removeForm(int formId)
	{
		PreparedStatement sqlStatement = null;
        try {
            String query = "DELETE FROM forms WHERE form_id=?";

            sqlStatement = database_connection.prepareStatement(query);

            sqlStatement.setInt(1, formId);

            sqlStatement.executeUpdate();
            System.out.println("Form deleted");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}
	
	// ----------------------------------------------------------------------

	public int addQuestion(int formId, FormType type, String label, Object default_value)
	{
		PreparedStatement sqlStatement = null;
		 try {
		 	 // add the default answer. It doesn't have a submission attached, so it's -1

			 String query = "INSERT INTO questions (form_id,q_type,label,default_answer_id) VALUES (?,?,?,-1)";

			 sqlStatement = database_connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			 sqlStatement.setInt(1, formId);
			 sqlStatement.setString(2, type.toString());
			 sqlStatement.setString(3, label);

			 sqlStatement.executeUpdate();
			 ResultSet resultSet = sqlStatement.getGeneratedKeys();


			 if (resultSet.next()) {
			 	 // we've added the new question. Get the id of the question
				 int question_id = resultSet.getInt(1);
				 // set the new default answer in our answer set
				 int default_answer_id = addAnswer(question_id, -1, default_value );

				 // update our newly set question line with our default answer id
				 String query2 = "UPDATE questions SET default_answer_id = ? WHERE question_id=?";

				 sqlStatement = database_connection.prepareStatement(query2);

				 sqlStatement.setInt(1, default_answer_id);
				 sqlStatement.setInt(2, question_id);

				 sqlStatement.executeUpdate();

				 sqlStatement.close();

				 // and finally return the question id
				 return question_id;
			 }
			 if (sqlStatement != null) {
                sqlStatement.close();
            }
		 } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        
        return -1;
	}

	public boolean updateQuestion(int questionId, FormType type, String label, Object default_value)
	{
		// this is a little more complicated. We need to compare the new default value to the old one to determine if we need to add it

		PreparedStatement sqlStatement = null;

        try {
        	// first retrieve the existing default
			Object current_default = getAnswer(type,questionId,-1);
			String query;
			if(!current_default.equals(default_value))
			{
				// we've changed the default, so remove the old one and add the new one
				removeAnswer(questionId,-1,type);
				int answer_id = addAnswer(questionId,-1,default_value);

				query = "UPDATE questions SET q_type = ?, label = ?, default_answer_id = ? WHERE question_id=?";

				sqlStatement = database_connection.prepareStatement(query);

				sqlStatement.setString(1, type.toString());
				sqlStatement.setString(2, label);
				sqlStatement.setInt(3, answer_id);
				sqlStatement.setInt(4, questionId);
			}
			else {
				// we haven't changed the default so we don't need to set it
				query = "UPDATE questions SET q_type = ?, label = ? WHERE question_id=?";

				sqlStatement = database_connection.prepareStatement(query);

				sqlStatement.setString(1, type.toString());
				sqlStatement.setString(2, label);
				sqlStatement.setInt(3, questionId);
			}

            sqlStatement.executeUpdate();
            System.out.println("Question updated");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}

	public boolean removeQuestion(int questionId)
	{
		PreparedStatement sqlStatement = null;
        try {
            String query = "DELETE FROM questions WHERE question_id=?";

            sqlStatement = database_connection.prepareStatement(query);

            sqlStatement.setInt(1, questionId);

            sqlStatement.executeUpdate();
            System.out.println("Question deleted");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}
	
	// ----------------------------------------------------------------------
	
	public int addSubmission(int formId, int submitterId, int subjectId, Date currentDate)
	{
		PreparedStatement sqlStatement = null;
		 try {
			 String query = "INSERT INTO submissions (form_id,submitter_id,subject_id, date) VALUES (?,?,?,?)";

			 sqlStatement = database_connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			 sqlStatement.setInt(1, formId);
			 sqlStatement.setInt(2, submitterId);
			 sqlStatement.setInt(3, subjectId);
			 sqlStatement.setDate(4, currentDate);

			 sqlStatement.executeUpdate();
			 ResultSet resultSet = sqlStatement.getGeneratedKeys();
			
			 if (resultSet.next()) {
				 return resultSet.getInt(1);
			 }
			 if (sqlStatement != null) {
               sqlStatement.close();
           }
		 } catch (SQLException e) {
           e.printStackTrace();
           System.out.println(e.getMessage());

       }
       
       return -1;
	}

	// originally didn't have an Update for this - it was originally just a link table.
	// However, submissions can have some info of their own - particularly the date of the last update

	public boolean updateSubmission(int submissionId, int submitterId, int subjectId, Date currentDate)
	{
		PreparedStatement sqlStatement = null;
		try {
			String query = "UPDATE submissions SET submitter_id = ?, subject_id = ?, date = ? WHERE submission_id=?";

			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setInt(1, submitterId);
			sqlStatement.setInt(2, subjectId);
			sqlStatement.setDate(3, currentDate);
			sqlStatement.setInt(4, submissionId);

			sqlStatement.executeUpdate();
			System.out.println("Submission updated");

			sqlStatement.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}
		return false;
	}

	public boolean removeSubmission(int submissionId)
	{
		PreparedStatement sqlStatement = null;
        try {
            String query = "DELETE FROM submissions WHERE submission_id=?";

            sqlStatement = database_connection.prepareStatement(query);

            sqlStatement.setInt(1, submissionId);

            sqlStatement.executeUpdate();
            System.out.println("Submission deleted");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}
	
	// ----------------------------------------------------------------------
	
	public int addAnswer(int questionId, int submissionId, Object value)
	{
		String valueType = value.getClass().getSimpleName();
		String destination_table = "answer_" + valueType.toLowerCase();
		
		PreparedStatement sqlStatement = null;
		 try {
			 String query = "INSERT INTO " + destination_table + " (question_id,submission_id,value) VALUES (?,?,?)";

			 sqlStatement = database_connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			 sqlStatement.setInt(1, questionId);
			 sqlStatement.setInt(2, submissionId);

			 switch(valueType)
			 {
			 case "String": {
				 sqlStatement.setString(3, (String) value);
			 }
			 break;
			 case "Integer": {
				 sqlStatement.setInt(3, (Integer)value);
			 }
			 break;
			 case "Float": {
				 sqlStatement.setFloat(3, (Float)value);
			 }
			 break;
			 case "Boolean": {
				 sqlStatement.setBoolean(3, (Boolean)value);
			 }
			 break;
			 }

			 sqlStatement.executeUpdate();
			 ResultSet resultSet = sqlStatement.getGeneratedKeys();
			
			 if (resultSet.first()) {
				 return resultSet.getInt(1);
			 }
			 if (sqlStatement != null) {
              sqlStatement.close();
          }
		 } catch (SQLException e) {
          e.printStackTrace();
          System.out.println(e.getMessage());

      }
      
      return -1;
	}

	public boolean updateAnswer(int questionId, int submissionId, Object value)
	{
		String valueType = value.getClass().getSimpleName();
		String destination_table = "answer_" + valueType.toLowerCase();
		
		PreparedStatement sqlStatement = null;
        try {
            String query = "UPDATE " + destination_table +" SET value = ? WHERE question_id = ? AND submission_id = ?";

            sqlStatement = database_connection.prepareStatement(query);

            switch(valueType)
			 {
			 case "String": {
				 sqlStatement.setString(1, (String) value);
			 }
			 break;
			 case "Integer": {
				 sqlStatement.setInt(1, (Integer)value);
			 }
			 break;
			 case "Float": {
				 sqlStatement.setFloat(1, (Float)value);
			 }
			 break;
			 case "Boolean": {
				 sqlStatement.setBoolean(1, (Boolean)value);
			 }
			 break;
			 }
            sqlStatement.setInt(2, questionId);
            sqlStatement.setInt(3, submissionId);

            sqlStatement.executeUpdate();
            System.out.println("Answer updated");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}

	public boolean removeAnswer(int questionId, int submissionId, FormType type)
	{
		String valueType = type.toString();
		String destination_table = "answer_" + valueType.toLowerCase();
		
		PreparedStatement sqlStatement = null;
        try {
            String query = "DELETE FROM " + destination_table + " WHERE question_id=? AND submission_id=?";

            sqlStatement = database_connection.prepareStatement(query);

            sqlStatement.setInt(1, questionId);
            sqlStatement.setInt(2, submissionId);

            sqlStatement.executeUpdate();
            System.out.println("Submission deleted");
            
            sqlStatement.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return false;
	}

	public Object getAnswer(FormType type, int questionId, int submissionId)
	{
		// simple retrieval method
		PreparedStatement sqlStatement = null;

		Object value = null;
		String typeString = type.toString().toLowerCase();
		String query = "SELECT * FROM answer_" + typeString + " WHERE question_id = ? AND submission_id = ?";

		try {
			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setInt(1, questionId);
			sqlStatement.setInt(2, submissionId);

			ResultSet resultSet = sqlStatement.executeQuery();

			if(resultSet.next()) {
				switch(type) {
					case FT_STRING:
					{
						value = resultSet.getString("value");
					}
					break;

					case FT_INT:
					{
						value = resultSet.getInt("value");
					}
					break;

					case FT_FLOAT:
					{
						value = resultSet.getFloat("value");
					}
					break;

					case FT_BOOLEAN:
					{
						value = resultSet.getBoolean("value");
					}
					break;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return value;
	}
	
	// ----------------------------------------------------------------------
	
	// utility functions
	
	public Collection<FormElement> getFormElements(int formId)
	{
		ArrayList<FormElement> outputList = new ArrayList<FormElement>();
		
		PreparedStatement sqlStatement = null;
		 try {
			 String query = "SELECT * FROM questions WHERE form_id = ? ORDER BY question_id ASC";
			
			 sqlStatement = database_connection.prepareStatement(query);
			 sqlStatement.setInt(1, formId);
			 
			 ResultSet resultSet = sqlStatement.executeQuery();
			
			 while (resultSet.next()){
	                FormElement newElement = new FormElement();
	                
	                newElement.question_id = resultSet.getInt("question_id");
	                newElement.label = resultSet.getString("label");
	                String type = resultSet.getString("q_type");
	                newElement.type = FormType.fromString(type);

	                // retrieve default value answer id (submission id -1)
	                int default_answer_id = resultSet.getInt("default_answer_id");
	                newElement.default_value = getAnswer(newElement.type,newElement.question_id,-1);

	                outputList.add(newElement);
	            }
			 
			 if (sqlStatement != null) {
                sqlStatement.close();
            }
		 } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
		
		return outputList;
	}
	
	public int submitFormAnswers(int formId, int submitterId, int subjectId, List<Object> values)
	{
		int submissionId = addSubmission(formId,submitterId, subjectId, new java.sql.Date(System.currentTimeMillis()));

		ArrayList<FormElement> elements = (ArrayList)getFormElements(formId);

		for(int i=0;i<elements.size();i++)
		{
			Object value = values.get(i);
			FormElement element = elements.get(i);
			int answerId = addAnswer(element.question_id, submissionId, value);
		}

		return submissionId;
	}

	public Map<Date, Collection<FormElement>> getSubmissionsByDate(int formId, int patientID)
	{
		HashMap<Date, Collection<FormElement>> output = new HashMap<Date, Collection<FormElement>>();

		Map<Date,Integer> submissions = getSubmissionsForPatient(formId, patientID);

		for(Map.Entry<Date,Integer> entry : submissions.entrySet())
		{
			Date entryDate = entry.getKey();
			Integer submissionId = entry.getValue();

			Collection<FormElement> submission = getSubmission(formId, submissionId);

			output.put(entryDate, submission);
		}

		return output;
	}

	public Collection<FormElement> getSubmission(int formId, int submissionId)
	{
		// get the array structure
		ArrayList<FormElement> elements = (ArrayList)getFormElements(formId);

		PreparedStatement sqlStatement = null;

		// for each element, get the relevant value
		for(FormElement fe : elements)
		{
			FormType type = fe.type;
			String typeString = type.toString().toLowerCase();
			String query = "SELECT * FROM answer_" + typeString + " WHERE question_id = ? AND submission_id = ?";

			try {
				sqlStatement = database_connection.prepareStatement(query);

				sqlStatement.setInt(1, fe.question_id);
				sqlStatement.setInt(2, submissionId);

				ResultSet resultSet = sqlStatement.executeQuery();

				if(resultSet.next()) {
					switch(fe.type) {
						case FT_STRING:
						{
							fe.value = resultSet.getString("value");
						}
						break;

						case FT_INT:
						{
							fe.value = resultSet.getInt("value");
						}
						break;

						case FT_FLOAT:
						{
							fe.value = resultSet.getFloat("value");
						}
						break;

						case FT_BOOLEAN:
						{
							fe.value = resultSet.getBoolean("value");
						}
						break;
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			finally {
				if (sqlStatement != null) {
					try {
						sqlStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return elements;
	}


	public Map<Date,Integer> getSubmissionsForPatient(int formID, int patientID)
	{
		HashMap<Date,Integer> output = new HashMap<Date,Integer>();

		PreparedStatement sqlStatement = null;

		String query = "SELECT submission_id,date FROM submissions WHERE subject_id = ? AND form_id = ? ORDER BY date DESC";

		try {
			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setInt(1, patientID);
			sqlStatement.setInt(2, formID);

			ResultSet resultSet = sqlStatement.executeQuery();

			while(resultSet.next()) {
				output.put(resultSet.getDate("date"), resultSet.getInt("submission_id"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return output;
	}

	public Map<String, Integer> getFormByNameAndPatient(String name, int patient_id)
	{
		HashMap<String,Integer> forms = new HashMap<>();
		PreparedStatement sqlStatement = null;

		String query = "SELECT * FROM submissions JOIN forms ON submissions.form_id = forms.form_id WHERE forms.form_name = ? AND submissions.subject_id = ?";

		try {
			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setString(1, name);
			sqlStatement.setInt(2, patient_id);

			ResultSet resultSet = sqlStatement.executeQuery();

			while(resultSet.next()) {
				forms.put(resultSet.getString("forms.form_name"), resultSet.getInt("forms.form_id"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return forms;
	}

	public Map<String, Integer> getFormByTypeAndPatient(String user_type, int patient_id)
	{
		HashMap<String,Integer> forms = new HashMap<>();
		PreparedStatement sqlStatement = null;

		String query = "SELECT * FROM submissions JOIN user ON submissions.submitter_id = user.userId JOIN forms ON submissions.form_id = forms.form_id WHERE submissions.subject_id = ? AND user.userType = ?";

		try {
			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setInt(1, patient_id);
			sqlStatement.setString(2, user_type);


			ResultSet resultSet = sqlStatement.executeQuery();

			while(resultSet.next()) {
				forms.put(resultSet.getString("forms.form_name"), resultSet.getInt("forms.form_id"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return forms;
	}

	public Map<String, Integer> getFormsForGP(int id)
	{
		return getFormsForUserType("gp",id);
	}

	public Map<String, Integer> getFormsForUserType(String type,int id)
	{
		HashMap<String,Integer> forms = new HashMap<>();

		PreparedStatement sqlStatement = null;

		String query = "SELECT form_id,form_name FROM forms WHERE userId = ?";

		try {
			sqlStatement = database_connection.prepareStatement(query);

			sqlStatement.setInt(1, id);

			ResultSet resultSet = sqlStatement.executeQuery();

			while(resultSet.next()) {
				forms.put(resultSet.getString("form_name"), resultSet.getInt("form_id"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return forms;
	}

}
