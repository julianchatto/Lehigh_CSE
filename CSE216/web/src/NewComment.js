import { useState } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import api from './api/posts';

/**
 * Page to create a new post
 * @returns the NewPost component
 */
const NewComment = () => {
    const { id } = useParams(); // Use useParams to access the post ID from the URL
    const history = useHistory();
    const [commentText, setCommentText] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [commentLink, setCommentLink] = useState(''); 
    const [commentFile, setCommentFile] = useState(null);
    const [base64, setBase64] = useState('');

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                setBase64(e.target.result);
            };
            reader.readAsDataURL(file);
        }
        setCommentFile(file);
    };


    // Handle form submission
    const handleSubmit = async (e) => {
        setIsSubmitting(true);
        e.preventDefault();
        try {
            await api.post(`/posts/${id}/comments`, {
                Text: commentText,
                webURL: commentLink,
                file: base64, 
                fileName: commentFile.name
            }, {
                headers: {
                    'Cache-Control': "private, max-age=604800"
                },
            });
            
            setCommentFile(null);
            setCommentLink('');
            setCommentText('');
            history.push(`/post/${id}`); // Redirect back to the post page

        } catch (err) {
            console.error("Failed to update comment", err);
        }
        setIsSubmitting(false);

    };
    return (
         <form onSubmit={handleSubmit} className="NewPost newPostForm">
            <label htmlFor="commentText">Text:</label>
            <textarea
                id="commentText"
                required
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
            ></textarea>
            <label htmlFor="commentLink">Link:</label>
            <input
                id="commentLink"
                type="url"
                value={commentLink}
                onChange={(e) => setCommentLink(e.target.value)}
            />
            <label htmlFor="commentFile">File:</label>
            <input
                id="commentFile"
                type="file"
                onChange={(e) => handleFileChange(e)}
            />
            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Commenting..." : "Comment"}
            </button>
        </form>
    );
}

export default NewComment;