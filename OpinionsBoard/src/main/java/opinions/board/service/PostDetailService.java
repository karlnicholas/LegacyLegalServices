package opinions.board.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;
import opinions.board.model.BoardReply;

@Stateless
public class PostDetailService {
	@Inject private EntityManager em;
	public PostDetailService() {}
	public PostDetailService(EntityManager em) {
		this.em = em;
	}
	
	public BoardPost getBoardPostDetails(BoardPost boardPost) {
		BoardPost bp = em.createQuery("select distinct bp from BoardPost bp left outer join fetch bp.boardComments where bp = :boardPost", BoardPost.class)
			.setParameter("boardPost", boardPost)
			.getSingleResult();
		return bp;
	}
	public BoardComment updateBoardPost(BoardPost boardPost) {
		return null;
	}
	public void createNewBoardComment(BoardComment boardComment) {
		em.persist(boardComment);
	}
	public BoardComment getBoardCommentDetail(BoardComment boardComment ) {
		return null;
	}
	public BoardComment deleteBoardComment(BoardComment boardComment) {
		return null;
	}
	public BoardComment updateBoardComment(BoardComment boardComment) {
		return null;
	}
	public BoardReply createNewBoardReply(BoardReply boardReply) {
		return null;
	}
	public BoardReply updateBoardReply(BoardReply boardReply) {
		return null;
	}
	public BoardReply deleteBoardReply(BoardReply boardReply) {
		return null;
	}

	public void deletePostDependents(BoardPost boardPost) {
		// see if any work to do
		if ( boardPost == null || boardPost.getBoardComments() == null )
			return;
		Query dq = em.createQuery("delete from BoardReply br where br.boardComment = :boardComment");
		for ( BoardComment boardComment: boardPost.getBoardComments() ) {
			dq.setParameter("boardComment", boardComment).executeUpdate();
			em.remove(boardComment);
		}
	}
}
