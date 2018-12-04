package opinions.board.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import opinions.board.model.BoardPost;
import opinions.board.service.PostListingService;

@SuppressWarnings("serial")
@WebServlet({""})
public class OpinionsBoardContoller extends HttpServlet {
	@Inject private Logger logger;
	@EJB private PostListingService postListingService;
	private static final int defaultListingMaxResults = 10;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<BoardPost> posts = postListingService.getBoardPosts(defaultListingMaxResults);
		request.setAttribute("posts", posts);
		request.getRequestDispatcher("/WEB-INF/views/board.jsp").forward(request, response);
	}
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newPostText = request.getParameter("newPostText").trim();
		logger.fine(()-> "Posted: " + newPostText);
		if ( !newPostText.isEmpty() ) {
			BoardPost boardPost = new BoardPost();
			boardPost.setPostText(request.getParameter("newPostText"));
			postListingService.createNewBoardPost(boardPost);
		}
		response.sendRedirect("/board");
    }

}
