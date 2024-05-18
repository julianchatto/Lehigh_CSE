import { useParams, Link } from "react-router-dom";

const Comment = ({ key, comment, curUser}) => {
    const { id } = useParams();

    return (
        <article className="comment">
            {curUser.UserID === comment.UserID ? <Link to={`/profile`}><h4>Poster: {comment.UserName} </h4></Link> : <Link to={`/user/${comment.UserID}`}><button className="commentButton">Poster: {comment.UserName}</button></Link>}

            <br />
            <br />
            <Link to={`/post/${id}/comments/${comment.ID}`}><button className="commentButton">{comment.Text}</button></Link>
            <br />
            {comment.webURL !== '' && <a href={comment.webURL} target="_blank" rel="noreferrer">View Link</a>}
            <br />
            {comment.file !== '' && <a href={comment.fileURL} target="_blank" rel="noreferrer">View Attached File</a>}

        </article>
    )
}

export default Comment;