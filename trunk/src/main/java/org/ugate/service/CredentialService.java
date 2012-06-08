package org.ugate.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ugate.service.dao.CredentialDao;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.Role;

/**
 * Credential service
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class CredentialService {

	private static final Logger log = LoggerFactory.getLogger(CredentialService.class);
	// TODO : Until browsers support SHA-256 http://tools.ietf.org/html/rfc5843 we have to use MD5
	private static final String ALGORITHM = "MD5"; //"SHA-256";
	public static final String SALT = "Authorization";

	@Resource
	private CredentialDao credentialDao;
	
	/**
	 * @return a {@linkplain List} of all {@linkplain Actor}s
	 */
	public List<Actor> getAllActors() {
        return credentialDao.getAllActors();
	}
	
	/**
	 * @return the total number of {@linkplain Actor}s
	 */
	public long getActorCount() {
		return credentialDao.getActorCount();
	}

	/**
	 * Adds a user with the specified roles to a central data source
	 * 
	 * @param username
	 *            the user's login ID
	 * @param password
	 *            the user's password
	 * @param host
	 *            the {@linkplain Host} that will be associated with the user
	 * @param roles
	 *            the {@linkplain Role}(s) that the user should have
	 * @return the newly persisted {@linkplain Actor}
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Actor addUser(final String username, final String password, final Host host, final Role... roles) 
			throws UnsupportedOperationException {
		final Actor actor = new Actor();
		actor.setHost(host);
		actor.setLogin(username);
		String pwdHash = generateHash(username, password);
		actor.setPwd(pwdHash);
		actor.setRoles(new HashSet<Role>(Arrays.asList(roles)));
		credentialDao.persistActor(actor);
		return actor;
	}
	
	/**
	 * Gets an {@linkplain Actor} by login ID
	 * 
	 * @param username
	 *            the login ID
	 * @return the {@linkplain Actor}
	 */
	public Actor getActor(final String username) {
		return credentialDao.getActor(username);
	}

	/**
	 * Gets an {@linkplain Actor} by password
	 * 
	 * @param password
	 *            the password
	 * @return the {@linkplain Actor}
	 */
	public Actor getActorByPassword(final String password) {
		return credentialDao.getActorByPassword(password);
	}

	/**
	 * Authenticates a user against a central data source
	 * 
	 * @param username
	 *            the user's login ID
	 * @param password
	 *            the user's password
	 * @return the authenticated {@linkplain Actor} (or <code>null</code> when
	 *         authentication fails)
	 */
	public Actor authenticate(final String username, final String password) {
		try {
			final Actor actor = credentialDao.getActor(username);
			if (actor != null) {
				if (hasDigestMatch(username, actor.getPwd(), password)) {
					return actor;
				}
			} else if (log.isDebugEnabled()) {
				log.debug(String.format("No %1$s exists with a login of %2$s", username));
			}
		} catch (final Exception e) {
			if (e instanceof NoResultException || (e.getCause() != null && e.getCause() instanceof NoResultException)) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("Cannot authenticate %1$s because they do not exist", username), e);
				} else {
					log.info(String.format("Cannot authenticate %1$s because they do not exist", username));
				}
			} else {
				log.error(String.format("Unable to authenticate user %1$s", username), e);
			}
		}
		return null;
	}
	
	/**
	 * Merges the {@linkplain Host}
	 * 
	 * @param host
	 *            the {@linkplain Host} to merge
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void mergeHost(final Host host) {
		credentialDao.mergeEntity(host);
	}

	/**
	 * Determines if two passwords match for a specified login ID
	 * 
	 * @param username
	 *            the login ID
	 * @param hashedPassword
	 *            the hashed password
	 * @param rawPassword
	 *            the un-hashed password to compare to
	 * @return true when the passwords match
	 */
	public static boolean hasDigestMatch(final String username, final String hashedPassword, final String rawPassword) {
		final byte[] digest1 = getBytes(hashedPassword);
		final byte[] digest2 = getBytes(generateHash(username, rawPassword));
		return MessageDigest.isEqual(digest2, digest1);
	}
	
	/**
	 * @see #digestBytes(String, String)
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return the digested user name and password
	 */
	protected static String generateHash(final String username, final String password) {
		try {
			return toString(digestBytes(username, password), 16);
		} catch (final Exception e) {
			log.warn("Unable to digest password for " + username, e);
			return null;
		}
	}
	
	/**
	 * Digests the bytes of a user name and password
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return the digested user name and password bytes
	 */
	protected static byte[] digestBytes(final String username, final String password) {
		try {
			MessageDigest sha = MessageDigest.getInstance(ALGORITHM);
			sha.update(getBytes(getSaltedPassword(username, password)));
			return sha.digest();
		} catch (final Exception e) {
			log.warn("Unable to create message digest for " + ALGORITHM, e);
			return null;
		}
	}

	/**
	 * Gets bytes of a {@linkplain String}
	 * 
	 * @param string
	 *            the {@linkplain String} to get bytes for
	 * @return the bytes
	 */
	protected static byte[] getBytes(final String string) {
		try {
			return string.getBytes("ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			log.error("Unable to get bytes for " + string, e);
			return null;
		}
	}
	
	/**
	 * Converts bytes into a {@linkplain String}
	 * 
	 * @param bytes
	 *            the bytes to convert
	 * @param base
	 *            the base to convert them to
	 * @return the {@linkplain String}
	 */
	protected static String toString(final byte[] bytes, final int base) {
		final StringBuilder buf = new StringBuilder();
		for (final byte b : bytes) {
			int bi = 0xff & b;
			int c = '0' + (bi / base) % base;
			if (c > '9') {
				c = 'a' + (c - '0' - 10);
			}
			buf.append((char) c);
			c = '0' + bi % base;
			if (c > '9') {
				c = 'a' + (c - '0' - 10);
			}
			buf.append((char) c);
		}
		return buf.toString();
	}
	
	/**
	 * Gets a salted password
	 * 
	 * @param username
	 *            the login ID
	 * @param password
	 *            the password
	 * @return the salted password
	 */
	protected static final String getSaltedPassword(final String username, final String password) {
		// Do not change the salt generation below or web digest use will be rendered unusable!
		return username + ':' + SALT + ':' + password;
	}
}