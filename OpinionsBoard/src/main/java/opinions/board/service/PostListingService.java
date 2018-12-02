package opinions.board.service;

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
		List<BoardPost> listings = em.createQuery("select p from BoardPost p", BoardPost.class).setMaxResults(count).getResultList();
		if ( listings == null ) {
			return new ArrayList<>();
		}
		return listings;
	}
	public void createNewBoardPost(BoardPost boardPost) {
		em.persist(boardPost);
	}
	
	public BoardPost getBoardPostDetail(BoardPost boardPost) {
		return em.createQuery("select b from BoardPost b left outer join fetch b.boardComments where b = :boardPost", BoardPost.class)
				.setParameter("boardPost", boardPost)
				.getSingleResult();
	}
	
	public void deleteBoardPost(BoardPost boardPost) {
		PostDetailService postDetailService = new PostDetailService(em);
		postDetailService.deletePostDependents(getBoardPostDetail(boardPost));
		em.remove(boardPost);
	}
}
