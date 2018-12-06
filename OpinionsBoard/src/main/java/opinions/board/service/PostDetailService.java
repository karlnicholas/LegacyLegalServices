package opinions.board.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
	
	public List<BoardComment> getBoardComments(BoardPost boardPost, int maxResult) {
		return em.createQuery("select bc from BoardComment bc where bc.boardPost = :boardPost order by bc.date desc", BoardComment.class)
			.setParameter("boardPost", boardPost)
			.setMaxResults(maxResult)
			.getResultList();
	}
	public void createNewBoardComment(BoardComment boardComment) {
		// persist first ... hopefully equals on id.
		if ( boardComment.getDate() == null ) {
			boardComment.setDate(LocalDateTime.now());
		}
		em.persist(boardComment);
	}
	public List<BoardReply> getBoardReplies(BoardComment boardComment, int maxResult) {
		return em.createQuery("select br from BoardReply br where br.boardComment = :boardComment order by br.date desc", BoardReply.class)
				.setParameter("boardComment", boardComment)
				.setMaxResults(maxResult)
				.getResultList();
	}
	public void deleteBoardComment(BoardComment boardComment) {
		em.createQuery("delete from BoardReply br where br.boardComment = :boardComment")
		.setParameter("boardComment", boardComment)
		.executeUpdate();
		em.createQuery("delete from BoardComment bc where bc = :boardComment")
		.setParameter("boardComment", boardComment)
		.executeUpdate();
	}
	public BoardComment updateBoardComment(BoardComment boardComment) {
		return em.merge(boardComment);
	}
	public void createNewBoardReply(BoardReply boardReply) {
		if ( boardReply.getDate() == null ) {
			boardReply.setDate(LocalDateTime.now());
		}
		em.persist(boardReply);
	}
	public BoardReply updateBoardReply(BoardReply boardReply) {
		return em.merge(boardReply);
	}
	public void deleteBoardReply(BoardReply boardReply) {
		em.createQuery("delete from BoardReply br where br = : boardReply")
		.setParameter("boardReply", boardReply)
		.executeUpdate();
	}

}
