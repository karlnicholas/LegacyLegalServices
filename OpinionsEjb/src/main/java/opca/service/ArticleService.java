package opca.service;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opca.model.Article;
import opca.model.ArticleComment;
import opca.model.User;

@Stateless
public class ArticleService {
	@Inject private EntityManager em;
	
    @PermitAll
	public List<Article> getArticles() {
		return em.createNamedQuery(Article.GET_ARTICLES_AND_COMMENTS, Article.class).getResultList();
	}

    @RolesAllowed({"USER"})
	public void postComment(Article article, User user, String comment) {
		ArticleComment c = new ArticleComment(article);
		c.setComment(comment);
		c.setUser(user);
		c.setImportance(1);
		em.persist(c);
	}

    @RolesAllowed({"USER"})
	public void mergeComment(ArticleComment comment) {
		em.merge(comment);		
	}
}
