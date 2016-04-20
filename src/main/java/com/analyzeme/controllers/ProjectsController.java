package com.analyzeme.controllers;

import com.analyzeme.parsers.InfoToJson;
import com.analyzeme.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ольга on 16.03.2016.
 */
@RestController
public class ProjectsController {

	/**
	 * gets files from project by project id
	 *
	 * @param projectName
	 * @return list of fileNames
	 * null if userRepository doesn't exist or project doesn't exist
	 * @throws IOException
	 */
	@RequestMapping(value = "{user_id}/project/{project_id}/files", method = RequestMethod.GET)
	public List<String> getFiles(@PathVariable("user_id") int userId,
			@PathVariable("project_id") String projectName)
			throws IOException {
		try {
			if (UsersRepository.getRepo().checkInitialization() == null) {
				return null;
			}
			//this line will return all filenames in project, including temporary deleted files
			return UsersRepository.getRepo().findUser(userId).getProjects().findProjectById(projectName).getFilenames();
			//to get only active files use:
			//ArrayList<String> filenames = UsersRepository.repo.findUser("guest").projects.findProject(projectName).returnAllNames();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * creates new project
	 *
	 * @param projectName - should be passed as header
	 * @return project unique name
	 * null if project wasn't created
	 * @throws IOException
	 */
	@RequestMapping(value = "{user_id}/project/new/create", method = RequestMethod.PUT)
	public String createProject(@PathVariable("user_id") int userId,
								@RequestHeader("project_name") String projectName) throws IOException {
		try {
			//when other users created, CheckInitializationAndCreate() should be called from user creator only
			//now it's possible to create a default user here
			UsersRepository.getRepo().checkInitializationAndCreate();
			if (UsersRepository.getRepo().findUser("guest") == null) {
				//login, email, password
				String[] param = {"guest", "guest@mail.sth", "1234"};
				UsersRepository.getRepo().newItem(param);
			}
			//now username is used here
			//to use userId just change "guest" to int with it
			String project = UsersRepository.getRepo().newProject(userId, projectName);
			if (project == null) return null;
			else return project;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * deletes project by project name
	 *
	 * @param uniqueName
	 * @return HttpStatus.NOT_FOUND if userRepository doesn't exist
	 * HttpStatus.OK if project deleted successfully
	 * HttpStatus.BAD_REQUEST if sth went wrong
	 */
	@RequestMapping(value = "{user_id}/project/{unique_name}/delete", method = RequestMethod.DELETE)
	public HttpStatus deleteProjectById(@PathVariable("user_id") int userId ,@PathVariable("unique_name") String uniqueName)
			throws IOException {
		try {
			if (UsersRepository.getRepo().checkInitialization() == null) {
				//response.setHeader("Success", "project doesn't exist");
				return HttpStatus.NOT_FOUND;
			}
			//to change to deleting by id use ...projects.deleteProjectById(projectId)
			//deleteProject or deleteProjectById deactivate project and all files in it
			//to remove them completely use deleteProjectCompletely or deleteProjectCompletelyById
			return (UsersRepository.getRepo().findUser(userId).getProjects().deleteProjectById(uniqueName) ?
					HttpStatus.OK : HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return HttpStatus.BAD_REQUEST;
		}
	}

	/**
	 * gets all projects for "guest" user
	 */
	@RequestMapping(value = "/user/projects", method = RequestMethod.GET)
	public List<String> getProjectNames() throws IOException {
		try {
			if (UsersRepository.getRepo().checkInitialization() == null) {
				return null;
			}
			//this returns all projects (temporary deleted included)
			//to get only active projects use ...returnAllActiveProjectsNames()
			return UsersRepository.getRepo().findUser("guest").getProjects().returnAllProjectsNames();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * returns info about all projects
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = "/projects/info", method = RequestMethod.GET)
	public String getProjectsInfo() throws IOException {
		try {
			if (UsersRepository.getRepo().checkInitialization() == null) {
				return null;
			}
			return InfoToJson.convert(UsersRepository.getRepo().findUser("guest").getProjects().getProjects());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
