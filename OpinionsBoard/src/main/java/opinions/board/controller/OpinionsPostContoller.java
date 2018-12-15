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

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;
import opinions.board.model.BoardReply;
import opinions.board.service.PostDetailService;
import opinions.board.service.PostListingService;

@SuppressWarnings("serial")
@WebServlet({"/post"})
public class OpinionsPostContoller extends HttpServlet {
	@Inject private Logger logger;
	@EJB private PostListingService postListingService;
	@EJB private PostDetailService postDetailService;
	private static final int defaultListingMaxResults = 10;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String postId = request.getParameter("postId");		
		String commentDetail = request.getParameter("commentDetail");		
		logger.fine(()-> "Post Id: " + postId + " commentDetail: " + commentDetail);
		if ( postId != null && !postId.isEmpty() ) {
			Long id = Long.decode( postId.trim() );
			BoardPost boardPost = postListingService.getBoardPost(id);
			request.setAttribute("boardPost", boardPost);
			List<BoardComment> comments = postDetailService.getBoardComments(boardPost, defaultListingMaxResults);
			request.setAttribute("comments",  comments);
			if ( commentDetail != null && !commentDetail.isEmpty() ) {
				Long commentId = Long.decode( commentDetail.trim() );
				request.setAttribute("commentDetail", commentDetail);
				for ( BoardComment comment: comments) {
					if ( comment.getId() == commentId ) {
						List<BoardReply> replies = postDetailService.getBoardReplies(comment, defaultListingMaxResults);
						request.setAttribute("replies", replies);
						break;
					}
				}
			}
		}
		request.getRequestDispatcher("/WEB-INF/views/post.jsp").forward(request, response);
	}
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newPostText = request.getParameter("newPostText");
		String postId = request.getParameter("postId");
		logger.fine(()-> "Post Id: " + postId + " Post Text: " + newPostText);
		if ( newPostText != null 
			&& !newPostText.isEmpty()
			&& postId != null 
			&& !postId.isEmpty() 
		) {
			BoardPost boardPost = postListingService.getBoardPost(Long.decode( postId.trim() ));
			BoardComment boardComment = new BoardComment();
			boardComment.setBoardPost(boardPost);
			boardComment.setCommentText(newPostText);
			postDetailService.createNewBoardComment(boardComment);
		}
		response.sendRedirect("/board/post?postId=" + postId);
    }

}
