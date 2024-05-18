import { useParams, Link } from "react-router-dom";
import { useContext, useState, useEffect } from 'react';
import DataContext from './context/DataContext';
import useAxiosFetch from './hooks/useAxiosFetch';
import Comment from './Comment.js';


const backendURL = "http://127.0.0.1:4567";
/**
 * Page for when a user clicks on a post
 * @returns the PostPage component
 */
const PostPage = ( user ) => {
    const { posts } = useContext(DataContext);
    const { id } = useParams();
    const post = posts.find(post => post.ID === Number(id));
    const [comments, setComments] = useState([]);

    
    const { data, fetchError, isLoading } = useAxiosFetch(`${backendURL}/posts/${id}/comments`)

    useEffect(() => {
        setComments(data);
    }, [data]);
    /**
     * Renders the PostPage component
     */
    return (
        <main className="PostPage">
            <article className="post">
                {isLoading && <h2>Loading...</h2>}
                {post && !fetchError && !isLoading &&
                    <>
                        <h2>Message: {post.Subject}</h2>
                        <p className="postBody">Subject: {post.Message}</p>
                        <Link to={`/post/${id}/comments`}><button className="newCommentButton">New Comment</button></Link>
                        <br />
                        <br />
                    </> 
                }
                {data && data.length !==0 && !fetchError && !isLoading &&
                    <>
                        Comments:
                        {comments.map(comment => (
                            <Comment key={comment.ID} comment={comment} curUser={user} />
                        ))}
                    </>
                }
                {!post &&
                    <>
                        <h2>Post Not Found</h2>
                        <p>
                            <Link to='/'>Back</Link>
                        </p>
                    </>
                }
                {data.length === 0 && !isLoading && <h2>No Comments Yet</h2>}

            </article>
        </main>
    )
}

export default PostPage