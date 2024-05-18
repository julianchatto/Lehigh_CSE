import { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import api from './api/posts';

const EditComment = () => {
    const { id, commentID } = useParams(); // Use useParams to access the commentID and post ID from the URL
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

    // Fetch the existing comment using the useEffect hook
    useEffect(() => {
        const fetchComment = async () => {
            try {
                const response = await api.get(`/posts/${id}/comments/${commentID}`);
                setCommentText(response.data.mData.Text); // Set the fetched comment text to state
                setCommentFile(null);
                setCommentLink(response.data.mData.webURL);
            } catch (err) {
                console.error("Failed to fetch comment", err);
            }
        };
        fetchComment();
    }, [id, commentID]);

    // Handle form submission
    const handleSubmit = async (e) => {
        setIsSubmitting(true);
        e.preventDefault();
        try {
            // Update the comment on the backend
            await api.put(`/posts/${id}/comments/${commentID}`, {
                Text: commentText,
                webURL: commentLink,
                file: base64, 
                fileName: commentFile.name
            }, {
                headers: {
                    'Cache-Control': "private, max-age=604800"
                },
            });

            history.push(`/post/${id}`); // Redirect back to the post page
            setCommentFile(null);
            setCommentLink('');
            setCommentText('');
        } catch (err) {
            console.error("Failed to update comment", err);
        }
        setIsSubmitting(false);

    };
    return (
         <form onSubmit={handleSubmit} className="NewPost newPostForm">
            <textarea
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
                {isSubmitting ? "Updating..." : "Update Comment"}
            </button>
        </form>
    );
};

export default EditComment;
