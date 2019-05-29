/**
 * 
 */
package Health_System_Monitoring;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author user
 * This DAO interface permits users to read and write editable forms
 * Values for the results are entered in object-form so we can return the list of results as Objects
 */
public interface FormDao {
	
	// CRUD functions
	
	/**
	 * Add new Form to the DB
	 * @param creator User that created the form (userId)
	 * @param form_name Name of created form
	 * @return id of created form
	 */
	public int addNewForm(User creator, String form_name);
	/**
	 * Update form info
	 * @param formId to update
	 * @param creator value
	 * @return success
	 */
	public boolean updateForm(int formId, User creator, String form_name);
	/**
	 * Remove form from DB
	 * @param formId to remove
	 * @return success
	 */
	public boolean removeForm(int formId);
	
	// ----------------------------------------------------------------------
	
	/**
	 * Add question to specified Form
	 * @param formId to add question to
	 * @param type of the question
	 * @param label the question text
	 * @param default_value the default value
	 * @return id of the created question
	 */
	public int addQuestion(int formId, FormType type, String label, Object default_value);
	/**
	 * Update question in form
	 * @param questionId to update
	 * @param type of the question
	 * @param label the question text
	 * @param default_value the default value to add
	 * @return success
	 */
	public boolean updateQuestion(int questionId, FormType type, String label, Object default_value);
	/**
	 * Remove question from form
	 * @param questionId to remove
	 * @return success
	 */
	public boolean removeQuestion(int questionId);
	
	// ----------------------------------------------------------------------

	/**
	 * Add a submission to the DB
	 * @param formId submission relates to
	 * @param submitterId from user table
	 * @param subjectId from patient table
	 * @param currentDate the date of the update
	 * @return id of the created submission
	 */
	public int addSubmission(int formId, int submitterId, int subjectId, Date currentDate);

	/**
	 * Updates a submission in the DB
	 * @param submissionId that we're changing
	 * @param submitterId from user table
	 * @param subjectId from patient table
	 * @param currentDate the date of the update
	 * @return success
	 */
	public boolean updateSubmission(int submissionId, int submitterId, int subjectId, Date currentDate);

	/**
	 * Remove a submission from the DB
	 * @param submissionId to remove
	 * @return success
	 */
	public boolean removeSubmission(int submissionId);
	
	// ----------------------------------------------------------------------
	
	/**
	 * Add an answer to the answer tables
	 * @param questionId we're answering
	 * @param submissionId which number response
	 * @param value we're adding (type determines destination table)
	 * @return id of answer in the destination table
	 */
	public int addAnswer(int questionId, int submissionId, Object value);
	/**
	 * Update an answer in the answer tables
	 * @param questionId question we're updating
	 * @param submissionId the answer's from
	 * @param value we're updating to
	 * @return success
	 */
	public boolean updateAnswer(int questionId, int submissionId, Object value);
	/**
	 * Remove answer from the answer tables
	 * @param questionId question we're removing an answer to
	 * @param submissionId submission we're removing the answer to
	 * @param type of answer we'e removing
	 * @return success
	 */
	public boolean removeAnswer(int questionId, int submissionId, FormType type);

	/**
	 * Get a specific answer from the answer tables
	 * @param type type of answer
	 * @param questionId id of the question this is an answer to
	 * @param submissionId id of the submission this is
	 * @return Object of the answer
	 */
	public Object getAnswer(FormType type, int questionId, int submissionId);
	
	// ----------------------------------------------------------------------
	
	// utility functions

	public Map<String, Integer> getFormsForGP(int gp_id);

	/**
	 * Get structure of specified Form
	 * @param formId to retrieve
	 * @return ArrayList of FormElement objects (without answers)
	 */
	public Collection<FormElement> getFormElements(int formId);
	
	/**
	 * Submit form answers for specified Form
	 * @param formId to submit an answer to
	 * @param submitterId that is submitting the answers
	 * @param values list of values from the screen form, in same order as returned from getFormElements
	 * @return submission id
	 */
	public int submitFormAnswers(int formId, int submitterId, int subjectId, List<Object> values);

	/**
	 * Get form structure and answers for a given Submission
	 * @param formId specific form to retrieve
	 * @param submissionId specific submission to retrieve
	 * @return ArrayList of FormElement objects (with answers folded in)
	 */
	public Collection<FormElement> getSubmission(int formId, int submissionId);

	/**
	 * Get list of submissions for the given patient and the given form
	 * @param formID that we're looking for
	 * @param patientID from the patient table
	 * @return a Map of the data, with the date as the key and the submission ID as the value
	 */
	public Map<Date,Integer> getSubmissionsForPatient(int formID, int patientID);


	/**
	 * Get list o
	 * @param user_type
	 * @param patient_id
	 * @return
	 */
	public Map<String, Integer> getFormByTypeAndPatient(String user_type, int patient_id);

	public Map<String, Integer> getFormByNameAndPatient(String name, int patient_id);

	/**
	 * Get the complete list of answers for a given form and patient, arranged by date
	 * @param formId we're looking for
	 * @param patientID from the patient table
	 * @return a Map of the data, with the Date as the key and the answers as the values
	 */
	public Map<Date, Collection<FormElement>> getSubmissionsByDate(int formId, int patientID);
}
