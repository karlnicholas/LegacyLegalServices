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
	
	public BoardPost deleteBoardPost(BoardPost boardPost) {
		return null;
	}
	
}
