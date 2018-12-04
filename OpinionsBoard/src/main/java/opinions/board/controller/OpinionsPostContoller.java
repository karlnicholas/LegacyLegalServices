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
		String postId = request.getParameter("postId").trim();		
		String commentDetail = request.getParameter("commentDetail").trim();		
		logger.fine(()-> "Post Id: " + postId + " commentDetail: " + commentDetail);
		if ( !postId.isEmpty() ) {
			Long id = Long.decode( postId );
			BoardPost boardPost = postListingService.getBoardPost(id);
			request.setAttribute("boardPost", boardPost);
			List<BoardComment> comments = postDetailService.getBoardComments(boardPost, defaultListingMaxResults);
			request.setAttribute("comments",  comments);
			if ( commentDetail != null ) {
				Long commentId = Long.decode( commentDetail );
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
		String newPostText = request.getParameter("newPostText").trim();
		String postId = request.getParameter("postId").trim();
		logger.fine(()-> "Post Id: " + postId + " Post Text: " + newPostText);
		if ( !newPostText.isEmpty() && !postId.isEmpty() ) {
			BoardPost boardPost = postListingService.getBoardPost(Long.decode( postId ));
			BoardComment boardComment = new BoardComment();
			boardComment.setBoardPost(boardPost);
			boardComment.setCommentText(newPostText);
			postDetailService.createNewBoardComment(boardComment);
		}
		response.sendRedirect("/board/post?postId=" + postId);
    }

}
