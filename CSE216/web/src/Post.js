import { Link } from "react-router-dom";
import { useContext } from 'react';
import api from './api/posts';
import DataContext from './context/DataContext';
 
/**
 * Renders each individual post component
 * @param {Number} key 
 * @param {Post} post for the post object
 * @returns the Post component
 */
const Post = ({ key, post, curUser }) => {
    const { posts, setPosts } = useContext(DataContext);

    const handleUpVote = async () => {
        try {
            const getResponse = await api.get(`/posts/${post.ID}/votes`); // got the users vote
            let vote = getResponse.data.mData;
            if (vote === 0) { // they are netural
                await api.put(`/posts/${post.ID}/votes`, {VoteType: 1}) // upvote
            } else { // they already liked it or disliked it
                await api.put(`/posts/${post.ID}/votes`, {VoteType: 0});
            }
            if (vote === 1) {
                post.VoteSum--;
            } else {
                post.VoteSum++;
            }
            setPosts(posts.map(p => p));
        } catch (err) {
            console.log(`Error: ${err.message}`);
        }
    }
    const handleDownVote = async () => {
        try {
            const getResponse = await api.get(`/posts/${post.ID}/votes`); // got the users vote
            let vote = getResponse.data.mData;
            if (vote === 0) { // they are netural
                await api.put(`/posts/${post.ID}/votes`, {VoteType: -1}) // downvote
            } else { // they already liked it or disliked it
                await api.put(`/posts/${post.ID}/votes`, {VoteType: 0});
            }
            if (vote === -1) {
                post.VoteSum++;
            } else {
                post.VoteSum--;
            }
            setPosts(posts.map(p => p));
        } catch (err) {
            console.log(`Error: ${err.message}`);
        }
    }

    /**
     * Renders the Post component
     */
    return (
        <article className="post">
            {(curUser.UserID === post.UserID) ? <Link to={`/profile`}><h4>Poster: {post.UserName}</h4></Link> : <Link to={`/user/${post.UserID}`}><h4>Poster: {post.UserName}</h4></Link>}
            <br />
            <Link to={`/post/${post.ID}`}>
                <h2>{post.Subject}</h2>
            </Link>
            <p className="postBody">{
                (post.Message).length <= 25
                ? post.Message
                : `${(post.Message).slice(0, 25)}...`  // only display the first 25 characters
            }</p>
            {post.webURL !== '' && <a href={post.webURL} target="_blank" rel="noreferrer">View Link</a>}
            {post.file !== '' && <a href={post.fileURL} target="_blank" rel="noreferrer">View Attached File</a>} 
            <br />
            <br />
            {post.base64 !== "" && <img src={`data:image/png;base64,${post.base64}`} alt="Attached " width="100" height="100"/>}

            <p className="postDate">
                <button className="LikeButton" onClick={handleUpVote}>
                    👍
                </button>
                {post.VoteSum}
                <button className="LikeButton" onClick={handleDownVote}>👎</button>
            </p>

        </article>
    )
}


export default Post