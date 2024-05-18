import Post from './Post';

/**
 * Creates the feed of posts
 * @param {Post} posts 
 * @returns the Feed component
 */
const Feed = ({ posts, user }) => {
    return (
        <>
            {posts.map(post => (
                <Post key={post.ID} post={post} curUser={user} />
            ))}
        </>
    )
}

export default Feed