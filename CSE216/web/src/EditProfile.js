import { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import api from './api/posts';

const EditProfile = ({ user }) => {
   const history = useHistory();
    const [profileName, setProfileName] = useState("");
    const [profileEmail, setProfileEmail] = useState("");
    const [profileGI, setProfileGI] = useState("");
    const [profileSO, setProfileSO] = useState("");
    const [profileNote, setProfileNote] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    
    // console.log(user.Name);
    
    // Fetch the existing comment using the useEffect hook
    useEffect(() => {
        const fetchUser = async () => {
            try {

                setProfileName(user.Name); 
                setProfileEmail(user.Email); 
                setProfileGI(user.GI); 
                setProfileSO(user.SO); 
                setProfileNote(user.Role);            
            } catch (err) {
                console.error("Failed to fetch comment", err);
            } 
        };
        fetchUser();
    }, [user]);

    // Handle form submission
    const handleSubmit = async (e) => {
        setIsSubmitting(true);
        e.preventDefault();
        try {
            let result = await api.put(`/users/${user.UserID}`, {
                Name: profileName,
                Email: profileEmail,
                GI: profileGI,
                SO: profileSO,
                Role: profileNote,
            }, {
                headers: {
                    'Cache-Control': "private, max-age=604800"
                },
            });
            user.Name = profileName;
            user.Email = profileEmail;
            user.GI = profileGI;
            user.SO = profileSO;
            user.Role = profileNote;
            
            console.log(result);
            history.push(`/profile`); // Redirect back to the profile page

        } catch (err) {
            console.error("Failed to update comment", err);
        }
        setIsSubmitting(false);

    };
    return (
         <form onSubmit={handleSubmit} className="NewPost newPostForm">
            <textarea
                required
                value={profileName}
                onChange={(e) => setProfileName(e.target.value)}
            ></textarea>
            <textarea
                required
                value={profileEmail}
                onChange={(e) => setProfileEmail(e.target.value)}
            ></textarea>
            <textarea
                required
                value={profileGI}
                onChange={(e) => setProfileGI(e.target.value)}
            ></textarea>
            <textarea
                required
                value={profileSO}
                onChange={(e) => setProfileSO(e.target.value)}
            ></textarea>
            <textarea
                required
                value={profileNote}
                onChange={(e) => setProfileNote(e.target.value)}
            ></textarea>
            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Updating..." : "Update Profile"}
            </button>
        </form>
    );
};

export default EditProfile;