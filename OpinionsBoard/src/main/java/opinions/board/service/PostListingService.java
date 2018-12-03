package opinions.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opinions.board.model.*;

@Stateless
public class PostListingService {
	@Inject private EntityManager em;
	
	public List<BoardPost> getBoardPosts(int count) {
		List<BoardPost> listings = em.createQuery("select bp from BoardPost bp order by bp.date desc", BoardPost.class).setMaxResults(count).getResultList();
		if ( listings == null ) {
			return new ArrayList<>();
		}
		return listings;
	}
	public void createNewBoardPost(BoardPost boardPost) {
		if ( boardPost.getDate() == null ) {
			boardPost.setDate(LocalDateTime.now());
		}
		em.persist(boardPost);
	}
	
	public BoardPost updateBoardPost(BoardPost boardPost) {
		return em.merge(boardPost);
	}

	public void deleteBoardPost(BoardPost boardPost) {
		List<BoardComment> comments = em.createQuery("select bc from BoardComment bc where bc.boardPost = :boardPost order by bc.date desc", BoardComment.class)
				.setParameter("boardPost", boardPost)
				.getResultList();
		if ( comments != null && comments.size() > 0 ) {
			for ( BoardComment boardComment: comments) {
				em.createQuery("delete from BoardReply br where br.boardComment = :boardComment").setParameter("boardComment", boardComment).executeUpdate();
				em.createQuery("delete from BoardComment bc where bc = :boardComment").setParameter("boardComment", boardComment).executeUpdate();
			}
		}
		em.createQuery("delete from BoardPost bp where bp = :boardPost").setParameter("boardPost", boardPost).executeUpdate();
	}
}
