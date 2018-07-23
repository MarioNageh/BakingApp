package android.mohamedalaa.com.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.databinding.FragmentRecipeStepsDetailBinding;
import android.mohamedalaa.com.bakingapp.model.Steps;
import android.mohamedalaa.com.bakingapp.utils.NetworkUtils;
import android.mohamedalaa.com.bakingapp.utils.StringUtils;
import android.mohamedalaa.com.bakingapp.viewmodel.RecipeStepsDetailViewModel;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

/**
 * Created by Mohamed on 7/20/2018.
 *
 */
public class RecipeStepsDetailFragment extends Fragment implements Player.EventListener {

    // --- Constants

    public static final String INTENT_KEY_STEPS_LIST = "INTENT_KEY_STEPS_LIST";
    public static final String INTENT_KEY_STEP_INDEX_CHOSEN = "INTENT_KEY_STEP_INDEX_CHOSEN";

    // --- Private Variables

    private RecipeStepsDetailViewModel viewModel;

    private FragmentRecipeStepsDetailBinding binding;

    private SimpleExoPlayer simpleExoPlayer;
    private TrackSelector trackSelector;
    private DataSource.Factory dataSourceFactory;
    private MediaSource videoSource;

    /** Used only in case of tablet */
    private List<Steps> tempStepsList;
    private int tempIndexChosen;

    public RecipeStepsDetailFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_recipe_steps_detail, container, false);
        View view = binding.getRoot();

        // Get view model instance
        viewModel = ViewModelProviders.of(this).get(RecipeStepsDetailViewModel.class);

        // Receive Intent Values
        receiveIntentValues();

        // layout specific changes
        setupLayoutSpecificChanges();

        // setup Xml Views
        setupXmlViews();

        // setup Xml Clicks
        setupXmlClicks();

        return view;
    }

    /** {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} */

    @SuppressWarnings("unchecked")
    private void receiveIntentValues() {
        if (viewModel.stepsList != null || getActivity() == null){
            return;
        }

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(INTENT_KEY_STEPS_LIST)
                && intent.hasExtra(INTENT_KEY_STEP_INDEX_CHOSEN)){
            viewModel.stepsList = (List<Steps>) intent.getSerializableExtra(INTENT_KEY_STEPS_LIST);
            viewModel.indexChosen = intent.getIntExtra(INTENT_KEY_STEP_INDEX_CHOSEN, 0);
        }
    }

    private void setupXmlViews(){
        // Only is null in case of tablet
        if (viewModel.stepsList == null){
            viewModel.stepsList = tempStepsList;
            tempStepsList = null;
            viewModel.indexChosen = tempIndexChosen;
        }

        // Short- && Full-Description
        Steps step = viewModel.stepsList.get(
                viewModel.indexChosen);
        binding.shortDescriptionTextView.setText(step.getShortDescription());
        binding.fullDescriptionTextView.setText(step.getDescription());

        // disable previous && next button, if needed
        if (viewModel.indexChosen == 0){
            binding.previousStepButton.setEnabled(false);
            binding.previousStepButton.setAlpha(0.5f);
            binding.nextStepButton.setEnabled(true);
            binding.nextStepButton.setAlpha(1f);
        }else if (viewModel.indexChosen == viewModel.stepsList.size() - 1){
            binding.previousStepButton.setEnabled(true);
            binding.previousStepButton.setAlpha(1);
            binding.nextStepButton.setEnabled(false);
            binding.nextStepButton.setAlpha(0.5f);
        }else {
            binding.previousStepButton.setEnabled(true);
            binding.nextStepButton.setEnabled(true);
            binding.previousStepButton.setAlpha(1f);
            binding.nextStepButton.setAlpha(1f);
        }

        // setup video or image or both don't exist make visibility GONE
        String videoURL = step.getVideoURL();
        String thumbnailURL = step.getThumbnailURL();
        if (thumbnailURL.contains("mp4")){
            // then it should 've been video and maybe the backend made an error
            videoURL = thumbnailURL;
            thumbnailURL = "";
        }
        if (! StringUtils.isNullOrEmpty(videoURL)){
            binding.frameLayout.setVisibility(View.VISIBLE);
            binding.simpleExoPlayerView.setVisibility(View.VISIBLE);
            binding.imageView.setVisibility(View.GONE);

            makeVideoSetups();
        }else if (! StringUtils.isNullOrEmpty(thumbnailURL)){
            binding.frameLayout.setVisibility(View.VISIBLE);
            binding.simpleExoPlayerView.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.VISIBLE);

            makeImageSetups();
        }else {
            binding.frameLayout.post(()
                    -> binding.frameLayout.setVisibility(View.GONE));
        }
    }

    private void setupXmlClicks(){
        binding.previousStepButton.setOnClickListener(view -> {
            viewModel.indexChosen -= 1;

            releasePlayer();

            setupXmlViews();
        });

        binding.nextStepButton.setOnClickListener(view -> {
            viewModel.indexChosen += 1;

            releasePlayer();

            setupXmlViews();
        });
    }

    private void setupLayoutSpecificChanges(){
        boolean isLandscape = ! getResources().getBoolean(R.bool.is_portrait);
        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
        if (isLandscape && isPhone){
            binding.frameLayout.post(() -> {
                // Setting height to match_parent will make it non-scrollable
                // so i thought of this workaround and it is awesome and scrolls ok.
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, binding.frameLayout.getHeight());
                binding.frameLayout.setLayoutParams(params);
            });
        }
    }

    /** {@link #setupXmlViews()} */

    private void makeVideoSetups(){
        if(simpleExoPlayer == null){
            if (getContext() == null){
                return;
            }

            // Getting urlAsString
            String urlAsString = viewModel.stepsList.get(
                    viewModel.indexChosen).getVideoURL();
            if (StringUtils.isNullOrEmpty(urlAsString)){
                urlAsString = viewModel.stepsList.get(
                        viewModel.indexChosen).getThumbnailURL();
            }

            // 1. Create a default TrackSelector
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(
                    bandwidthMeter);
            trackSelector = new DefaultTrackSelector(
                    videoTrackSelectionFactory);

            // 2. Create the player
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    getContext(), trackSelector);

            // Bind the player to the view.
            binding.simpleExoPlayerView.setPlayer(simpleExoPlayer);

            // Produces DataSource instances through which media data is loaded.
            dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                    Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);

            // This is the MediaSource representing the media to be played.
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(urlAsString));

            simpleExoPlayer.addListener(this);

            // Prepare the player with the source.
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void makeImageSetups(){
        // Getting urlAsString
        String urlAsString = viewModel.stepsList.get(
                viewModel.indexChosen).getThumbnailURL();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_load)
                .error(R.drawable.ic_error);
        Glide.with(this)
                .load(urlAsString)
                .apply(requestOptions)
                .into(binding.imageView);
    }

    // ---- Overridden onResume() && onPause() && onDestroy() && onSaveInstanceState()

    @Override
    public void onResume() {
        super.onResume();

        if (simpleExoPlayer != null){
            simpleExoPlayer.seekTo(viewModel.videoPlayerPosition);
            simpleExoPlayer.setPlayWhenReady(viewModel.lastVideoStateOfPlayWhenReady/*true*/);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (simpleExoPlayer != null){
            viewModel.lastVideoStateOfPlayWhenReady = simpleExoPlayer.getPlayWhenReady();
            // Pause video when exit, continue playing when exit only done for music not video.
            simpleExoPlayer.setPlayWhenReady(false);
            viewModel.videoPlayerPosition = simpleExoPlayer.getCurrentPosition();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePlayer();
    }

    // ---- Private helper methods

    private void releasePlayer() {
        // Ensure that no internet connection view is gone, in case it was turned to visible
        binding.poorInternetConnectionLinearLayout.setVisibility(View.GONE);

        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            dataSourceFactory = null;
            videoSource = null;
            trackSelector = null;
        }
    }

    // ---- Player.EventListener Implemented Listener


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // Error might be because of no internet connection, so check that and if that's true
        // tell that to user.
        if (! NetworkUtils.isCurrentlyOnline(getContext())){
            binding.poorInternetConnectionLinearLayout.setVisibility(View.VISIBLE);
            binding.retryButton.setOnClickListener(view -> {
                releasePlayer();

                setupXmlViews();
            });
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    // ---- Public Methods

    public void setStepList(List<Steps> stepList){
        this.tempStepsList = stepList;
    }

    public void setIndexChosen(int indexChosen){
        this.tempIndexChosen = indexChosen;
    }

    public void changeIndexChosen(int indexChosen){
        viewModel.indexChosen = indexChosen;

        releasePlayer();

        setupXmlViews();
    }

}
